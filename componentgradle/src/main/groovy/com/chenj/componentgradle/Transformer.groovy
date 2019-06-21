package com.chenj.componentgradle

import com.android.SdkConstants
import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

import java.util.jar.JarEntry
import java.util.jar.JarFile

class Transformer extends Transform {

    private List<String> mAppLikeList;

    @Override
    String getName() {
        return 'Transformer'
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        mAppLikeList = new ArrayList<>();
        inputs.each {
            TransformInput transformInput ->
                transformInput.jarInputs.each {
                    JarInput jarInput ->
                        def jarFile = new JarFile(jarInput.file)
                        Enumeration<JarEntry> classes = jarFile.entries()
                        while (classes.hasMoreElements()) {
                            JarEntry libClass = classes.nextElement()
                            String className = libClass.getName()
                            if (!className.startsWith("android/") && className.endsWith(".class") &&
                                    !className.startsWith("R\$") && "R.class" != className && !className.endsWith("BuildConfig.class")) {
                                println("jar classes className : " + className)
                                InputStream is = jarFile.getInputStream(libClass)
                                if (is.available() != 0){
                                    operateApplication(is)
                                }
                            }
                        }
                        def jarName = jarInput.name
                        def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                        if (jarName.endsWith(".jar")) {
                            jarName = jarName.substring(0, jarName.length() - 4)
                        }

                        def dest = outputProvider.getContentLocation(jarName + md5Name,
                                jarInput.contentTypes, jarInput.scopes, Format.JAR)

                        FileUtils.copyFile(jarInput.file, dest)
                }
                transformInput.directoryInputs.each{
                    DirectoryInput directoryInput ->
                        directoryInput.file.eachFile {
                            File file ->
                                operateApplication(file)
                        }
                        def dest = outputProvider.getContentLocation(directoryInput.name,
                                directoryInput.contentTypes, directoryInput.scopes,
                                Format.DIRECTORY)
                        FileUtils.copyDirectory(directoryInput.file, dest)
                }
        }
    }

    void operateApplication(InputStream is){
        ClassReader cr = new ClassReader(is)
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        ClassVisitor cv = new ApplicationFinder(cw, mAppLikeList)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
    }

    void operateApplication(File file){

        if (file.isDirectory()){
            file.eachFile {
                File f ->
                    operateApplication(f)
            }
            return
        }
        String name = file.name
        if (name.endsWith(".class") && !name.startsWith("R\$") &&
                "R.class" != name && "BuildConfig.class" != name) {
            ClassReader cr = new ClassReader(file.bytes)
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
            ApplicationInjectVisitor cv = new ApplicationInjectVisitor(cw, mAppLikeList)
            cr.accept(cv, ClassReader.EXPAND_FRAMES)
            byte[] code = cw.toByteArray()
            FileOutputStream fos = new FileOutputStream(
                    file.parentFile.absolutePath + File.separator + name)
            fos.write(code)
            fos.close()
        }
    }

    static class ApplicationFinder extends ClassVisitor {

        private List<String> mAppLikeList;

        ApplicationFinder() {
            super(Opcodes.ASM5)
        }

        ApplicationFinder(ClassVisitor cv, List<String> appLikeList) {
            super(Opcodes.ASM5, cv)
            mAppLikeList = appLikeList
        }

        @Override
        void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
            if (interfaces != null && interfaces.length != 0){
                for (String interfaceName : interfaces){
                    if ("com/chenj/iservice/IApplicationLike".equals(interfaceName)){
                        println("appLike name : " + name)
                        mAppLikeList.add(name)
                        break
                    }
                }
            }
        }
    }

    static class ApplicationInjectVisitor extends ClassVisitor {

        private List<String> mAppLikeList;
        private boolean isHostApplication;

        ApplicationInjectVisitor() {
            super(Opcodes.ASM5)
        }

        ApplicationInjectVisitor(ClassVisitor cv, List<String> list) {
            super(Opcodes.ASM5, cv)
            mAppLikeList = list
        }

        @Override
        void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
            if (superName.equals('android/app/Application')){
                isHostApplication = true
                println("Application Name ： " + name)
            }
        }

        @Override
        MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
            if (isHostApplication && "onCreate".equals(name)){
                return new LifeCycleInjectAdapter(mv, access, name, desc, mAppLikeList)
            }
            return mv
        }
    }

    static class LifeCycleInjectAdapter extends AdviceAdapter{

        private List<String> mAppLikeList;

        protected LifeCycleInjectAdapter(MethodVisitor mv, int access, String name, String desc, List<String> list) {
            super(Opcodes.ASM5, mv, access, name, desc)
            mAppLikeList = list
        }

        @Override
        protected void onMethodEnter() {
            super.onMethodEnter()
            int curIndex = 1
            for (String className : mAppLikeList) {
                println("class Method enter : " + className)
                mv.visitTypeInsn(NEW, className)
                mv.visitInsn(DUP)
                mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "()V", false)
                mv.visitVarInsn(ASTORE, curIndex)
                mv.visitVarInsn(ALOAD, curIndex)
                mv.visitMethodInsn(INVOKEVIRTUAL, className, "onCreate", "()V", false)
                curIndex++
            }
        }
    }
}