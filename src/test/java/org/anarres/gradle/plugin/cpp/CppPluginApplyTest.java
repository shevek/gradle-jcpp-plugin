package org.anarres.gradle.plugin.cpp;

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
public class CppPluginApplyTest {

    Project project;

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build();
    }

    @Test
    public void testApply() {
        project.apply(Collections.singletonMap("plugin", "java"));
        project.apply(Collections.singletonMap("plugin", "cpp"));
        assertTrue("Project is missing plugin", project.getPlugins().hasPlugin(CppPlugin.class));
        Task task = project.getTasks().findByName("cpp");
        assertNotNull("Project is missing cpp task", task);
        assertTrue("Cpp task is the wrong type", task instanceof DefaultTask);
        assertTrue("Cpp task should be enabled", ((DefaultTask) task).isEnabled());
    }
}
