package unit.modelarium.entities.environments;

import modelarium.Config;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.FunctionalEnvironmentGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static unit.modelarium.entities.environments.EnvironmentTestHelpers.*;

public class FunctionalEnvironmentGeneratorTest {
    @Test
    public void testGenerateEnvironment() {
        Environment environment = new Environment("testEnv", List.of());
        FunctionalEnvironmentGenerator generator = new FunctionalEnvironmentGenerator(config -> environment);
        Config config = syncedConfig(1, 1, 1);

        assertSame(environment, generator.generateEnvironment(config));
    }

    @Test
    public void testGenerateEnvironment_PassesConfigToFunction() {
        FunctionalEnvironmentGenerator generator = new FunctionalEnvironmentGenerator(config -> {
            assertEquals(42, config.populationSize());
            return emptyEnvironment();
        });

        Config config = Config.builder()
                .populationSize(42)
                .tickCount(1)
                .threadCount(1)
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .build();

        generator.generateEnvironment(config);
    }
}
