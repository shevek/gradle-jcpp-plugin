package org.anarres.gradle.plugin.jcpp;

import java.util.Collections;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author shevek
 */
public class JCppPluginApplyTest {

    Project project;

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build();
    }

    @Test
    public void testApply() {
        project.apply(Collections.singletonMap("plugin", "java"));
        project.apply(Collections.singletonMap("plugin", "jcpp"));
        assertTrue("Project is missing plugin", project.getPlugins().hasPlugin(JCppPlugin.class));
        Task task = project.getTasks().findByName("jcpp");
        assertNotNull("Project is missing jcpp task", task);
        assertTrue("JCpp task is the wrong type", task instanceof DefaultTask);
        assertTrue("JCpp task should be enabled", ((DefaultTask) task).isEnabled());
    }
}
