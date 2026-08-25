package unit.modelarium.entities.generators;

import modelarium.Config;
import modelarium.entities.Environment;
import modelarium.entities.generators.FunctionalEnvironmentGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static unit.modelarium.entities.EnvironmentTestHelpers.*;

public class FunctionalEnvironmentGeneratorTest {
    @Test
    public void testGenerateEnvironment() {
        Environment environment = new Environment("testEnv", List.of());
        FunctionalEnvironmentGenerator generator = new FunctionalEnvironmentGenerator((config, random) -> environment);
        Config config = syncedConfig(1, 1, 1);

        assertSame(environment, generator.generateEnvironment(config, new SplittableRandom()));
    }

    @Test
    public void testGenerateEnvironment_PassesConfigToFunction() {
        FunctionalEnvironmentGenerator generator = new FunctionalEnvironmentGenerator((config, random) -> {
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

        generator.generateEnvironment(config, new SplittableRandom());
    }
}
