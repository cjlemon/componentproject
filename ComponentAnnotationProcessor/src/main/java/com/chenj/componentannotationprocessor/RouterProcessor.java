package com.chenj.componentannotationprocessor;

import com.chenj.componentannotation.Host;
import com.chenj.componentannotation.Path;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
@SupportedAnnotationTypes({Constants.ANNOTATION_HOST, Constants.ANNOTATION_PATH})
public class RouterProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("==================== process =========================");
        if (set == null || set.isEmpty()){
            return false;
        }
        Set<? extends Element> hostSet = roundEnvironment.getElementsAnnotatedWith(Host.class);
        if (hostSet.size() != 1){
            System.out.println("host 数量必须为1");
            return false;
        }
        System.out.println("RouterProcessor =============");
        Iterator<? extends Element> iterator = hostSet.iterator();
        Element element = iterator.next();
        Host hostAnno = element.getAnnotation(Host.class);
        String host = hostAnno.value();
        Set<? extends Element> pathSet = roundEnvironment.getElementsAnnotatedWith(Path.class);

        TypeElement typeElement = (TypeElement) element;
        ClassName typeClassName = ClassName.get(typeElement);
        ClassName targetClassName = ClassName.get(typeClassName.packageName(), typeClassName.simpleName() + "$" + host + "$UIRouter");

        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addStatement("super()")
                .addModifiers(Modifier.PUBLIC);
        for (Element e : pathSet) {
            Path pathAnnotation = e.getAnnotation(Path.class);
            String path = pathAnnotation.value();
            builder.addStatement("registerPath($S, $T.class)", path, ClassName.get((TypeElement)e));
        }
        MethodSpec constructMethod = builder.build();

        MethodSpec overrideMethod1 = MethodSpec.methodBuilder("getHost")
                .addAnnotation(Override.class)
                .returns(String.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $S", host)
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(targetClassName)
                .superclass(ClassName.get("com.chenj.uirouter", "BaseRouter"))
                .addMethod(constructMethod)
                .addMethod(overrideMethod1)
                .build();
        try {
            JavaFile.builder(targetClassName.packageName(), typeSpec)
                    .build()
                    .writeTo(processingEnv.getFiler());
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
