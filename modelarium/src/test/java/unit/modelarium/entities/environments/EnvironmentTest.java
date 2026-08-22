package unit.modelarium.entities.environments;

import modelarium.entities.attributes.sets.mutable.MutableEnvironmentAttributeSet;
import modelarium.entities.environments.Environment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        MutableEnvironmentAttributeSet attributeSet = environmentAttributeSet(
                "env",
                "timing",
                new EnvironmentTickProperty("tick")
        );
        Environment environment = new Environment("env", List.of(attributeSet));

        assertSame(attributeSet, environment.getAttributeSet("timing"));
    }

    @Test
    public void testGetAttributeSetByIndex() {
        MutableEnvironmentAttributeSet attributeSet = emptyEnvironmentAttributeSet("env", "timing");
        Environment environment = new Environment("env", List.of(attributeSet));

        assertSame(attributeSet, environment.getAttributeSet(0));
    }


    @Test
    public void testGetEvent() {
        MutableEnvironmentAttributeSet attributeSet = environmentAttributeSetFromAttributes(
                "env", "weather", new AlwaysTriggeredEnvironmentEvent("storm"));
        Environment environment = new Environment("env", List.of(attributeSet));

        assertEquals("storm", environment.getEvent("weather", "storm").name());
    }

    @Test
    public void testGetRoutine() {
        MutableEnvironmentAttributeSet attributeSet = environmentAttributeSetFromAttributes(
                "env", "weather", new EmptyEnvironmentRoutine("cycle"));
        Environment environment = new Environment("env", List.of(attributeSet));

        assertEquals("cycle", environment.getRoutine("weather", "cycle").name());
    }

    @Test
    public void testGetProperty() {
        MutableEnvironmentAttributeSet attributeSet = environmentAttributeSet(
                "env", "timing", new EnvironmentTickProperty("tick"));
        Environment environment = new Environment("env", List.of(attributeSet));

        assertEquals("tick", environment.getProperty("timing", "tick").name());
    }

    @Test
    public void testAttributeCount() {
        MutableEnvironmentAttributeSet firstAttributeSet = environmentAttributeSet("env", "s1", new EnvironmentTickProperty("a"));
        MutableEnvironmentAttributeSet secondAttributeSet = environmentAttributeSet("env", "s2", new EnvironmentTickProperty("b"));
        Environment environment = new Environment("env", List.of(firstAttributeSet, secondAttributeSet));

        assertEquals(2, environment.attributeCount());
    }

    @Test
    public void testCreateContext() {
        MutableEnvironmentAttributeSet attributeSet = environmentAttributeSet(
                "env", "timing", new EnvironmentTickProperty("tick"));
        Environment environment = new Environment("env", List.of(attributeSet));

        createContextFor(environment);

        assertNotNull(environment.context());
        assertSame(environment, environment.context().getThisEntity());
    }

    @Test
    public void testCreateContext_CalledTwice_IllegalStateException() {
        Environment environment = new Environment("env", List.of(
                environmentAttributeSet("env", "timing", new EnvironmentTickProperty("tick"))));
        createContextFor(environment);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> createContextFor(environment));
        assertEquals("Context already created", exception.getMessage());
    }
}
