package unit.modelarium;

import modelarium.Config;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.environments.generators.EnvironmentGenerator;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.ConfigTestHelpers.*;

public class ConfigTest {
    @Test
    public void testBuilder_SetsAllFields() {
        DefaultAgentGenerator agentGenerator = agentGenerator();
        EnvironmentGenerator environmentGenerator = environmentGenerator();

        Config config = Config.builder()
                .populationSize(50)
                .tickCount(200)
                .threadCount(4)
                .threadTimeout(Duration.ofSeconds(30))
                .areThreadsSynced(false)
                .agentGenerator(agentGenerator)
                .environmentGenerator(environmentGenerator)
                .build();

        assertEquals(50, config.populationSize());
        assertEquals(200, config.tickCount());
        assertEquals(4, config.threadCount());
        assertEquals(Duration.ofSeconds(30), config.threadTimeout());
        assertFalse(config.areThreadsSynced());
        assertSame(agentGenerator, config.agentGenerator());
        assertSame(environmentGenerator, config.environmentGenerator());
    }

    @Test
    public void testBuilder_Defaults() {
        Config config = Config.builder()
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .build();

        assertEquals(100, config.populationSize());
        assertEquals(100, config.tickCount());
        assertEquals(2, config.threadCount());
        assertTrue(config.areThreadsSynced());
        assertNotNull(config.scheduler());
        assertNotNull(config.runLogDatabaseFactory());
    }

    @Test
    public void testBuilder_NullAgentGenerator_NullPointerException() {
        assertThrows(NullPointerException.class, () -> Config.builder()
                .environmentGenerator(environmentGenerator())
                .build());
    }

    @Test
    public void testBuilder_NullEnvironmentGenerator_NullPointerException() {
        assertThrows(NullPointerException.class, () -> Config.builder()
                .agentGenerator(agentGenerator())
                .build());
    }

    @Test
    public void testBuilder_ZeroPopulationSize_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Config.builder()
                .populationSize(0)
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .build());
    }

    @Test
    public void testBuilder_NegativeTickCount_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Config.builder()
                .tickCount(-1)
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .build());
    }

    @Test
    public void testBuilder_ZeroThreadCount_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Config.builder()
                .threadCount(0)
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .build());
    }

    @Test
    public void testToString() {
        Config config = syncedConfig(10, 10, 2);

        assertNotNull(config.toString());
    }
}
