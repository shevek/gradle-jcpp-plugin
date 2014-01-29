package org.anarres.gradle.plugin.jcpp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.anarres.cpp.LexerException;
import org.anarres.cpp.Preprocessor;
import org.anarres.cpp.PreprocessorListener;
import org.anarres.cpp.Source;
import org.anarres.cpp.Token;
import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Task;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.EmptyFileVisitor;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;

/**
 * A bare cpp task.
 *
 * You may use this to do arbitrary cpp processing without
 * necessarily applying the plugin.
 *
 * @author shevek
 */
public class JCppTask extends ConventionTask {

    private class Listener extends PreprocessorListener {

        @Override
        public void handleError(Source source, int line, int column, String msg) throws LexerException {
            String message = source + ":" + line + ":" + column + ": error: " + msg;
            throw new GradleException(message);
        }

        @Override
        protected void print(String msg) {
            getLogger().info(msg);
            throw new GradleException(msg);
        }
    }

    private final Listener listener = new Listener();

    @InputDirectory
    public File inputDir;
    @OutputDirectory
    public File outputDir;
    @InputFiles
    public List<File> systemIncludePath = new ArrayList<File>();
    @InputFiles
    public List<File> localIncludePath = new ArrayList<File>();

    @Input
    public String filter = "**/*.java";
    @Input
    public Map<String, String> macros = new HashMap<String, String>();

    @Nonnull
    private static List<String> toStringList(@Nonnull List<?> in) {
        List<String> out = new ArrayList<String>();
        for (Object i : in)
            out.add(String.valueOf(i));
        return out;
    }

    public JCppTask() {
        doLast(new Action<Task>() {

            @Override
            public void execute(Task task) {
                // We have to call the methods so the ConventionMapping can kick in.
                final File inputDir = getInputDir();
                final File outputDir = getOutputDir();
                final List<String> systemIncludePath = toStringList(getSystemIncludePath());
                final List<String> localIncludePath = toStringList(getLocalIncludePath());

                DefaultGroovyMethods.deleteDir(outputDir);
                outputDir.mkdirs();

                ConfigurableFileTree inputFiles = getProject().fileTree(inputDir);
                inputFiles.include(filter);
                getLogger().info("ConfigurableFileTree is " + inputFiles);
                inputFiles.visit(new EmptyFileVisitor() {
                    @Override
                    public void visitFile(FileVisitDetails fvd) {
                        try {
                            getLogger().info("Processing " + fvd.getFile());
                            File outputFile = fvd.getRelativePath().getFile(outputDir);
                            preprocess(fvd.getFile(), outputFile, systemIncludePath, localIncludePath);
                        } catch (LexerException e) {
                            throw new GradleException("Failed to process " + fvd, e);
                        } catch (IOException e) {
                            throw new GradleException("Failed to process " + fvd, e);
                        }
                    }
                });
            }
        });
    }

    private void preprocess(@Nonnull File input, @Nonnull File output,
            List<String> systemIncludePath, List<String> localIncludePath) throws IOException, LexerException {
        Preprocessor cpp = new Preprocessor();
        cpp.setListener(listener);
        for (Map.Entry<String, String> e : macros.entrySet()) {
            cpp.addMacro(e.getKey(), e.getValue());
        }
        cpp.setSystemIncludePath(systemIncludePath);
        cpp.setQuoteIncludePath(localIncludePath);

        File dir = output.getParentFile();
        FileUtils.forceMkdir(dir);
        FileWriter writer = null;
        try {
            cpp.addInput(input);
            writer = new FileWriter(output);
            for (;;) {
                Token tok = cpp.token();
                if (tok == null)
                    break;
                if (tok.getType() == Token.EOF)
                    break;
                writer.write(tok.getText());
            }
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Nonnull
    public File getInputDir() {
        return inputDir;
    }

    @Nonnull
    public File getOutputDir() {
        return outputDir;
    }

    @Nonnull
    public List<File> getSystemIncludePath() {
        return systemIncludePath;
    }

    @Nonnull
    public List<File> getLocalIncludePath() {
        return localIncludePath;
    }

}
