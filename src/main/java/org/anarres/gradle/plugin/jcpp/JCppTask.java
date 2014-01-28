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
import org.anarres.cpp.Token;
import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Task;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.EmptyFileVisitor;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;

/**
 * A bare cpp task.
 *
 * You may use this to do arbitrary cpp processing without
 * necessarily applying the plugin.
 *
 * @author shevek
 */
public class JCppTask extends DefaultTask {

    private class Listener extends PreprocessorListener {

        @Override
        protected void print(String msg) {
            getLogger().info(msg);
        }
    }

    private final Listener listener = new Listener();
    private final Map<String, String> macros = new HashMap<String, String>();

    @InputDirectory
    public File inputDir;
    @OutputDirectory
    public File outputDir;
    @Input
    public List<File> systemIncludePath = new ArrayList<File>();
    @InputDirectory
    public List<File> localIncludePath = new ArrayList<File>();

    @Input
    public String filter = "**/*.java";
    @Optional
    @InputDirectory
    public File includeDir;
    @Input
    public Map<String, Object> contextValues = new HashMap<String, Object>();

    public JCppTask() {
        doLast(new Action<Task>() {

            @Override
            public void execute(Task task) {
                DefaultGroovyMethods.deleteDir(outputDir);
                outputDir.mkdirs();

                ConfigurableFileTree inputFiles = getProject().fileTree(inputDir);
                inputFiles.include(filter);
                inputFiles.visit(new EmptyFileVisitor() {
                    @Override
                    public void visitFile(FileVisitDetails fvd) {
                        try {
                            File outputFile = fvd.getRelativePath().getFile(outputDir);
                            preprocess(fvd.getFile(), outputFile);
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

    @Nonnull
    private List<String> toStringList(@Nonnull List<?> in) {
        List<String> out = new ArrayList<String>();
        for (Object i : in)
            out.add(String.valueOf(i));
        return out;
    }

    private void preprocess(@Nonnull File input, @Nonnull File output) throws IOException, LexerException {
        Preprocessor cpp = new Preprocessor();
        cpp.setListener(listener);
        for (Map.Entry<String, String> e : macros.entrySet()) {
            cpp.addMacro(e.getKey(), e.getValue());
        }
        cpp.setSystemIncludePath(toStringList(systemIncludePath));
        cpp.setQuoteIncludePath(toStringList(localIncludePath));

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

}
