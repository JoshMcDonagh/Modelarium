package unit.modelarium.config;

import helpers.TestFixtures;
import modelarium.Config;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.environments.EnvironmentGenerator;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Config} and its builder.
 */
public class ConfigTest {

    @Test
    void builder_setsAllFieldsCorrectly() {
        DefaultAgentGenerator agentGen = TestFixtures.counterAgentGenerator();
        EnvironmentGenerator envGen = TestFixtures.simpleEnvironmentGenerator();

        Config config = Config.builder()
                .populationSize(50)
                .tickCount(200)
                .threadCount(4)
                .threadTimeout(Duration.ofSeconds(30))
                .areThreadsSynced(false)
                .agentGenerator(agentGen)
                .environmentGenerator(envGen)
                .build();

        assertEquals(50, config.populationSize());
        assertEquals(200, config.tickCount());
        assertEquals(4, config.threadCount());
        assertEquals(Duration.ofSeconds(30), config.threadTimeout());
        assertFalse(config.areThreadsSynced());
        assertSame(agentGen, config.agentGenerator());
        assertSame(envGen, config.environmentGenerator());
    }

    @Test
    void builder_usesReasonableDefaults() {
        Config config = Config.builder()
                .agentGenerator(TestFixtures.counterAgentGenerator())
                .environmentGenerator(TestFixtures.simpleEnvironmentGenerator())
                .build();

        assertEquals(100, config.populationSize(), "Default population size should be 100.");
        assertEquals(100, config.tickCount(), "Default tick count should be 100.");
        assertEquals(2, config.threadCount(), "Default thread count should be 2.");
        assertTrue(config.areThreadsSynced(), "Threads should be synced by default.");
        assertNotNull(config.scheduler(), "Default scheduler should not be null.");
        assertNotNull(config.runLogDatabaseFactory(), "Default log database factory should not be null.");
    }

    @Test
    void builder_rejectsNullAgentGenerator() {
        assertThrows(NullPointerException.class, () ->
                Config.builder()
                        .environmentGenerator(TestFixtures.simpleEnvironmentGenerator())
                        .build());
    }

    @Test
    void builder_rejectsNullEnvironmentGenerator() {
        assertThrows(NullPointerException.class, () ->
                Config.builder()
                        .agentGenerator(TestFixtures.counterAgentGenerator())
                        .build());
    }

    @Test
    void builder_rejectsZeroPopulationSize() {
        assertThrows(IllegalArgumentException.class, () ->
                Config.builder()
                        .populationSize(0)
                        .agentGenerator(TestFixtures.counterAgentGenerator())
                        .environmentGenerator(TestFixtures.simpleEnvironmentGenerator())
                        .build());
    }

    @Test
    void builder_rejectsNegativeTickCount() {
        assertThrows(IllegalArgumentException.class, () ->
                Config.builder()
                        .tickCount(-1)
                        .agentGenerator(TestFixtures.counterAgentGenerator())
                        .environmentGenerator(TestFixtures.simpleEnvironmentGenerator())
                        .build());
    }

    @Test
    void builder_rejectsZeroThreadCount() {
        assertThrows(IllegalArgumentException.class, () ->
                Config.builder()
                        .threadCount(0)
                        .agentGenerator(TestFixtures.counterAgentGenerator())
                        .environmentGenerator(TestFixtures.simpleEnvironmentGenerator())
                        .build());
    }

    @Test
    void config_isARecord() {
        // Sanity check: records produce equals/hashCode/toString for free.
        Config a = TestFixtures.syncedConfig(10, 10, 2);
        Config b = TestFixtures.syncedConfig(10, 10, 2);
        // Different generator instances → not equal, but toString should not throw
        assertNotNull(a.toString());
    }
}
