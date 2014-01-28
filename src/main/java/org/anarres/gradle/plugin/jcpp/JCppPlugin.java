package org.anarres.gradle.plugin.jcpp;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

/**
 * The jcpp plugin.
 *
 * This creates a default configuration which preprocesses files
 * according to the CppPluginExtension.
 *
 * @author shevek
 */
public class JCppPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        final JCppPluginExtension extension = project.getExtensions().create("jcpp", JCppPluginExtension.class);
        Task jcppTask = project.getTasks().create("jcpp", JCppTask.class, new Action<JCppTask>() {

            @Override
            public void execute(JCppTask task) {
                task.setDescription("Preprocesses C preprocessor files.");
                // TODO: This isn't lazy evaluation. :-(
                task.inputDir = project.file(extension.inputDir);
                task.outputDir = project.file(extension.outputDir);
                task.contextValues = extension.contextValues;
            }
        });

        project.getTasks().getByName("compileJava").dependsOn(jcppTask);
        SourceSetContainer sourceSets = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
        final SourceSet mainSourceSet = sourceSets.getByName("main");
        mainSourceSet.getJava().srcDir(extension.outputDir);
    }

}
