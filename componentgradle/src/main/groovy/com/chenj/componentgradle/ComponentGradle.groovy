package com.chenj.componentgradle

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComponentGradle implements Plugin<Project> {
    void apply(Project project) {
        String mainModule = null
        List<String> taskList = project.gradle.startParameter.taskNames
        String taskNames = taskList.toString()
        if (taskList.size() == 1 && taskList.get(0).toLowerCase().contains("assemble") && taskList.get(0).contains(":")) {
            mainModule = taskList.get(0).split(':')[1]
        }
        if (mainModule == null) {
            mainModule = project.rootProject.properties.get('mainModule')
        }
        project.extensions.create("relatedComponent", ComponentSetting)
        System.out.println("taskNames is " + taskNames)
        System.out.println("current module is " + project.name)
        System.out.println("main module is " + mainModule)
        boolean isBuildApk = isBuildApp(taskList)
        def runAlone = project.name.equals(mainModule) || !isBuildApk
        System.out.println(runAlone)
        if (runAlone) {
            project.apply plugin: 'com.android.application'
            project.android.sourceSets {
                main {
                    manifest.srcFile 'src/main/host/AndroidManifest.xml'
                    java.srcDirs = ['src/main/java', 'src/main/host/java']
                }
            }
            project.android.defaultConfig.applicationId = project.properties.get('applicationId');
            println("=====" + project.android.defaultConfig.applicationId)
            String relatedComponents = project.properties.get('relateComponents');
            if (relatedComponents != null && isBuildApk) {
                String[] components = relatedComponents.split(",");
                for (String dependency : components) {
                    project.dependencies.add("implementation", project.project(dependency))
                    println("======" + dependency)
                }
            }
            BaseExtension android = project.extensions.getByType(BaseExtension);
            android.registerTransform(new Transformer())
        } else {
            project.apply plugin: 'com.android.library'
        }

    }

    static boolean isBuildApp(List<String> taskList) {
        for (String taskName : taskList) {
            if (taskName.toLowerCase().contains("assemble")) {
                System.out.println("build app")
                return true
            }
        }
        System.out.println("not build app")
        return false
    }
}