package org.anarres.gradle.plugin.cpp;

import groovy.lang.Closure;
import java.util.HashMap;
import java.util.Map;

/**
 * The cpp plugin extension.
 *
 * This allows configuring the cpp plugin using a
 * <code>cpp { }</code> block.
 *
 * @author shevek
 */
public class CppPluginExtension {

    public static final String DEFAULT_INPUT_DIR = "src/main/cpp";
    public static final String DEFAULT_OUTPUT_DIR = "build/generated-sources/cpp";

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
