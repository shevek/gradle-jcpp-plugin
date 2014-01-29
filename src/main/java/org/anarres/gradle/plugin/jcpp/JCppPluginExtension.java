package org.anarres.gradle.plugin.jcpp;

import java.util.ArrayList;
import java.util.List;

/**
 * The jcpp plugin extension.
 *
 * This allows configuring the jcpp plugin using a
 * <code>jcpp { }</code> block.
 *
 * @author shevek
 */
public class JCppPluginExtension {

    public static final String DEFAULT_INPUT_DIR = "src/main/jcpp";
    public static final String DEFAULT_OUTPUT_DIR = "build/generated-sources/jcpp";

    public String inputDir = DEFAULT_INPUT_DIR;
    public String outputDir = DEFAULT_OUTPUT_DIR;
    public List<Object> systemIncludePath = new ArrayList<Object>();
    public List<Object> localIncludePath = new ArrayList<Object>();
}
