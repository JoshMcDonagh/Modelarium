package unit.modelarium.entities.immutable;

import helpers.TestFixtures;
import modelarium.entities.environments.Environment;
import modelarium.entities.immutable.ImmutableEnvironment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImmutableEnvironmentTest {
    @Test
    void immutableEnvironment_nameIsPreserved() {
        Environment env = TestFixtures.environmentWithTickCounter();
        ImmutableEnvironment immutable = new ImmutableEnvironment(env);

        assertEquals("env", immutable.name());
    }

    @Test
    void immutableEnvironment_attributeSetCountIsCorrect() {
        Environment env = TestFixtures.environmentWithTickCounter();
        ImmutableEnvironment immutable = new ImmutableEnvironment(env);

        assertEquals(1, immutable.attributeSetCount());
    }
}
