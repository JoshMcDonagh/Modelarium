package unit.modelarium.entities.immutable;

import modelarium.entities.environments.MutableEnvironment;
import modelarium.entities.environments.ImmutableEnvironment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static unit.modelarium.entities.immutable.ImmutableEntityTestHelpers.environmentWithTickCounter;

public class ImmutableEnvironmentTest {
    @Test
    public void testName() {
        MutableEnvironment environment = environmentWithTickCounter();
        ImmutableEnvironment immutableEnvironment = new ImmutableEnvironment(environment);

        assertEquals("environment", immutableEnvironment.name());
    }

    @Test
    public void testAttributeSetCount() {
        MutableEnvironment environment = environmentWithTickCounter();
        ImmutableEnvironment immutableEnvironment = new ImmutableEnvironment(environment);

        assertEquals(1, immutableEnvironment.attributeSetCount());
    }
}
