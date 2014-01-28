package org.anarres.gradle.plugin.cpp;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

/**
 * The cpp plugin.
 *
 * This creates a default configuration which preprocesses files
 * according to the CppPluginExtension.
 *
 * @author shevek
 */
public class CppPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        final CppPluginExtension extension = project.getExtensions().create("cpp", CppPluginExtension.class);
        Task cppTask = project.getTasks().create("cpp", CppTask.class, new Action<CppTask>() {

            @Override
            public void execute(CppTask task) {
                task.setDescription("Preprocesses C preprocessor files.");
                // TODO: This isn't lazy evaluation. :-(
                task.inputDir = project.file(extension.inputDir);
                task.outputDir = project.file(extension.outputDir);
                task.contextValues = extension.contextValues;
            }
        });

        project.getTasks().getByName("compileJava").dependsOn(cppTask);
        SourceSetContainer sourceSets = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
        final SourceSet mainSourceSet = sourceSets.getByName("main");
        mainSourceSet.getJava().srcDir(extension.outputDir);
    }

}
