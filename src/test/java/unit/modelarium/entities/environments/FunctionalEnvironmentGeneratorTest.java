package unit.modelarium.entities.environments;

import helpers.TestFixtures;
import modelarium.Config;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.FunctionalEnvironmentGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FunctionalEnvironmentGenerator}.
 */
public class FunctionalEnvironmentGeneratorTest {

    @Test
    public void testDelegatesToFunction() {
        Environment expected = new Environment("testEnv", List.of());

        FunctionalEnvironmentGenerator gen = new FunctionalEnvironmentGenerator(config -> expected);
        Config config = TestFixtures.syncedConfig(1, 1, 1);

        assertSame(expected, gen.generateEnvironment(config));
    }

    @Test
    public void testFunctionReceivesConfig() {
        FunctionalEnvironmentGenerator gen = new FunctionalEnvironmentGenerator(config -> {
            // Just verify the config is passed through
            assertNotNull(config);
            assertEquals(42, config.populationSize());
            return new Environment("e", List.of());
        });

        Config config = Config.builder()
                .populationSize(42)
                .tickCount(1)
                .threadCount(1)
                .agentGenerator(TestFixtures.counterAgentGenerator())
                .environmentGenerator(TestFixtures.simpleEnvironmentGenerator())
                .build();

        gen.generateEnvironment(config);
    }
}
