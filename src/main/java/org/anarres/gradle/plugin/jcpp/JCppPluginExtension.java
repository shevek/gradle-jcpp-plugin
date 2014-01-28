package org.anarres.gradle.plugin.jcpp;

import groovy.lang.Closure;
import java.util.HashMap;
import java.util.Map;

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
    public Map<String, Object> contextValues = new HashMap<String, Object>();

    /*
    public Map<String, Object> getContext() {
        return contextValues;
    }
    */

    void context(Map<String, Object> map) {
        contextValues.putAll(map);
    }

    void context(Closure<?> closure) {
        closure.setDelegate(contextValues);
        closure.call();
    }

}
