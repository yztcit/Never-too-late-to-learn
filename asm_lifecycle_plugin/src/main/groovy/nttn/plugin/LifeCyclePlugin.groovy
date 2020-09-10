package nttn.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public class LifeCyclePlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        System.out.println("=======> gradle plugin: LifeCyclePlugin <=======")

        def android = project.extensions.getByType(AppExtension)
        println '========= register LifeCycleTransform ==========='
        LifeTransform transform = new LifeTransform()
        android.registerTransform(transform)
    }
}