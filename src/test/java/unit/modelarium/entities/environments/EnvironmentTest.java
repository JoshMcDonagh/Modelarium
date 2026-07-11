package unit.modelarium.entities.environments;

import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.environments.Environment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static unit.modelarium.entities.environments.EnvironmentTestHelpers.*;

public class EnvironmentTest {
    @Test
    public void testName() {
        Environment environment = emptyEnvironment();

        assertEquals("env", environment.name());
    }

    @Test
    public void testAttributeSetCount_NoAttributeSets() {
        Environment environment = emptyEnvironment();

        assertEquals(0, environment.attributeSetCount());
    }

    @Test
    public void testGetAttributeSetByName() {
        EnvironmentAttributeSet attributeSet = environmentAttributeSet(
                "env",
                "timing",
                new EnvironmentTickProperty("tick")
        );
        Environment environment = new Environment("env", List.of(attributeSet));

        assertSame(attributeSet, environment.getAttributeSet("timing"));
    }

    @Test
    public void testGetAttributeSetByIndex() {
        EnvironmentAttributeSet attributeSet = emptyEnvironmentAttributeSet("env", "timing");
        Environment environment = new Environment("env", List.of(attributeSet));

        assertSame(attributeSet, environment.getAttributeSet(0));
    }
}
