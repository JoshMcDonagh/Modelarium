package unit.modelarium.entities.environments;

import helpers.TestAttributes;
import helpers.TestFixtures;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.environments.Environment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Environment} class.
 */
public class EnvironmentTest {

    @Test
    public void testEnvironmentNameIsAssigned() {
        Environment env = TestFixtures.emptyEnvironment();
        assertEquals("env", env.name());
    }

    @Test
    public void testEnvironmentWithNoAttributeSets() {
        Environment env = TestFixtures.emptyEnvironment();
        assertEquals(0, env.attributeSetCount());
    }

    @Test
    public void testEnvironmentAttributeSetAccessByName() {
        EnvironmentAttributeSet set = TestAttributes.environmentAttributeSet(
                "env", "timing", new TestAttributes.EnvironmentTickProperty("tick"));
        Environment env = new Environment("env", List.of(set));

        assertSame(set, env.getAttributeSet("timing"));
    }

    @Test
    public void testEnvironmentAttributeSetAccessByIndex() {
        EnvironmentAttributeSet set = TestAttributes.emptyEnvironmentAttributeSet("env", "timing");
        Environment env = new Environment("env", List.of(set));

        assertSame(set, env.getAttributeSet(0));
    }
}
