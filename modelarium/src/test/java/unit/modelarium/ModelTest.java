package unit.modelarium;

import modelarium.Config;
import modelarium.Model;
import modelarium.entities.agentsets.AgentSet;
import modelarium.entities.generators.AgentGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static unit.modelarium.ConfigTestHelpers.environmentGenerator;
import static unit.modelarium.ConfigTestHelpers.syncedConfig;

public class ModelTest {
    @Test
    public void testGetResults_BeforeRun_IllegalStateException() {
        Model model = new Model(syncedConfig(1, 1, 1));

        IllegalStateException exception = assertThrows(IllegalStateException.class, model::getResults);
        assertEquals("Results cannot be accessed before a model run has been completed", exception.getMessage());
    }

    @Test
    public void testRun_AgentGenerationFails_GeneratorIsReset() {
        AtomicInteger resetCount = new AtomicInteger();
        AgentGenerator generator = new AgentGenerator() {
            @Override
            public List<AgentSet> generateAgentsForEachThread(Config config, RandomGenerator random) {
                throw new IllegalStateException("generation failed");
            }

            @Override
            protected void reset() {
                resetCount.incrementAndGet();
            }
        };
        Config config = Config.builder()
                .populationSize(1)
                .tickCount(1)
                .threadCount(1)
                .agentGenerator(generator)
                .environmentGenerator(environmentGenerator())
                .build();

        assertThrows(IllegalStateException.class, () -> new Model(config).run());
        assertEquals(1, resetCount.get());
    }
}
