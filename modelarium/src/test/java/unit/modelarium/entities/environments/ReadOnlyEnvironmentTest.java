package unit.modelarium.entities.environments;

import modelarium.entities.Environment;
import modelarium.entities.readonly.ReadOnlyEnvironment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static unit.modelarium.entities.ReadOnlyEntityTestHelpers.environmentWithTickCounter;

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
