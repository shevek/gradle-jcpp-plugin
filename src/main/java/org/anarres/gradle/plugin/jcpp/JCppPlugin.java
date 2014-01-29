package org.anarres.gradle.plugin.jcpp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
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
        JCppTask jcppTask = project.getTasks().create("jcpp", JCppTask.class, new Action<JCppTask>() {

            @Nonnull
            private List<File> toList(@Nonnull List<Object> in) {
                List<File> out = new ArrayList<File>();
                for (Object i : in)
                    out.add(project.file(i));
                return out;
            }

            @Override
            public void execute(JCppTask task) {
                task.setDescription("Preprocesses C preprocessor files.");
                task.conventionMapping("inputDir", new Callable<File>() {
                    @Override
                    public File call() throws Exception {
                        return project.file(extension.inputDir);
                    }
                });
                task.conventionMapping("outputDir", new Callable<File>() {
                    @Override
                    public File call() throws Exception {
                        return project.file(extension.outputDir);
                    }
                });
                task.conventionMapping("systemIncludePath", new Callable<List<File>>() {
                    @Override
                    public List<File> call() throws Exception {
                        return toList(extension.systemIncludePath);
                    }
                });
                task.conventionMapping("localIncludePath", new Callable<List<File>>() {
                    @Override
                    public List<File> call() throws Exception {
                        return toList(extension.localIncludePath);
                    }
                });
            }
        });

        project.getTasks().getByName("compileJava").dependsOn(jcppTask);
        SourceSetContainer sourceSets = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
        final SourceSet mainSourceSet = sourceSets.getByName("main");
        mainSourceSet.getJava().srcDir(extension.outputDir);
    }

}
