package unit.modelarium.entities.immutable;

import modelarium.entities.environments.Environment;
import modelarium.entities.environments.ReadOnlyEnvironment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static unit.modelarium.entities.immutable.ReadOnlyEntityTestHelpers.environmentWithTickCounter;

public class ReadOnlyEnvironmentTest {
    @Test
    public void testName() {
        Environment environment = environmentWithTickCounter();
        ReadOnlyEnvironment immutableEnvironment = new ReadOnlyEnvironment(environment);

        assertEquals("environment", immutableEnvironment.name());
    }

    @Test
    public void testAttributeSetCount() {
        Environment environment = environmentWithTickCounter();
        ReadOnlyEnvironment immutableEnvironment = new ReadOnlyEnvironment(environment);

        assertEquals(1, immutableEnvironment.attributeSetCount());
    }
}
