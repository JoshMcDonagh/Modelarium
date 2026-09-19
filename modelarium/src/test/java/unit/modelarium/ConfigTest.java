package unit.modelarium;

import modelarium.Config;
import modelarium.entities.generators.DefaultAgentGenerator;
import modelarium.entities.generators.EnvironmentGenerator;
import modelarium.entities.logging.databases.factories.AttributeSetLogDatabaseFactory;
import modelarium.entities.logging.databases.factories.MemoryBasedAttributeSetLogDatabaseFactory;
import modelarium.scheduler.InOrderScheduler;
import modelarium.scheduler.Scheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void testBuilder_NonPositivePopulationSize_IllegalArgumentException(int populationSize) {
        assertThrows(IllegalArgumentException.class, () -> Config.builder()
                .populationSize(populationSize)
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .build());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void testBuilder_NonPositiveTickCount_IllegalArgumentException(int tickCount) {
        assertThrows(IllegalArgumentException.class, () -> Config.builder()
                .tickCount(tickCount)
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .build());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void testBuilder_NonPositiveThreadCount_IllegalArgumentException(int threadCount) {
        assertThrows(IllegalArgumentException.class, () -> Config.builder()
                .threadCount(threadCount)
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .build());
    }

    @Test
    public void testBuilder_NullScheduler_NullPointerException() {
        assertThrows(NullPointerException.class, () -> Config.builder()
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .scheduler(null)
                .build());
    }

    @Test
    public void testBuilder_NullRunLogDatabaseFactory_NullPointerException() {
        assertThrows(NullPointerException.class, () -> Config.builder()
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .runLogDatabaseFactory(null)
                .build());
    }

    @Test
    public void testBuilder_NullThreadTimeout_NullPointerException() {
        assertThrows(NullPointerException.class, () -> Config.builder()
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .threadTimeout(null)
                .build());
    }

    @Test
    public void testBuilder_NonPositiveThreadTimeout_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Config.builder()
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .threadTimeout(Duration.ZERO)
                .build());
        assertThrows(IllegalArgumentException.class, () -> Config.builder()
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .threadTimeout(Duration.ofNanos(-1))
                .build());
    }

    @Test
    public void testCanonicalConstructor_EnforcesAllInvariants() {
        DefaultAgentGenerator agentGenerator = agentGenerator();
        EnvironmentGenerator environmentGenerator = environmentGenerator();
        Scheduler scheduler = new InOrderScheduler();
        AttributeSetLogDatabaseFactory databaseFactory = new MemoryBasedAttributeSetLogDatabaseFactory();

        assertThrows(IllegalArgumentException.class, () -> directConfig(
                0, 1, 1, Duration.ofSeconds(1), agentGenerator, environmentGenerator, scheduler, databaseFactory));
        assertThrows(IllegalArgumentException.class, () -> directConfig(
                -1, 1, 1, Duration.ofSeconds(1), agentGenerator, environmentGenerator, scheduler, databaseFactory));
        assertThrows(IllegalArgumentException.class, () -> directConfig(
                1, 0, 1, Duration.ofSeconds(1), agentGenerator, environmentGenerator, scheduler, databaseFactory));
        assertThrows(IllegalArgumentException.class, () -> directConfig(
                1, -1, 1, Duration.ofSeconds(1), agentGenerator, environmentGenerator, scheduler, databaseFactory));
        assertThrows(IllegalArgumentException.class, () -> directConfig(
                1, 1, 0, Duration.ofSeconds(1), agentGenerator, environmentGenerator, scheduler, databaseFactory));
        assertThrows(IllegalArgumentException.class, () -> directConfig(
                1, 1, -1, Duration.ofSeconds(1), agentGenerator, environmentGenerator, scheduler, databaseFactory));
        assertThrows(NullPointerException.class, () -> directConfig(
                1, 1, 1, null, agentGenerator, environmentGenerator, scheduler, databaseFactory));
        assertThrows(IllegalArgumentException.class, () -> directConfig(
                1, 1, 1, Duration.ZERO, agentGenerator, environmentGenerator, scheduler, databaseFactory));
        assertThrows(IllegalArgumentException.class, () -> directConfig(
                1, 1, 1, Duration.ofNanos(-1), agentGenerator, environmentGenerator, scheduler, databaseFactory));
        assertThrows(NullPointerException.class, () -> directConfig(
                1, 1, 1, Duration.ofSeconds(1), null, environmentGenerator, scheduler, databaseFactory));
        assertThrows(NullPointerException.class, () -> directConfig(
                1, 1, 1, Duration.ofSeconds(1), agentGenerator, null, scheduler, databaseFactory));
        assertThrows(NullPointerException.class, () -> directConfig(
                1, 1, 1, Duration.ofSeconds(1), agentGenerator, environmentGenerator, null, databaseFactory));
        assertThrows(NullPointerException.class, () -> directConfig(
                1, 1, 1, Duration.ofSeconds(1), agentGenerator, environmentGenerator, scheduler, null));
    }

    @Test
    public void testToString() {
        Config config = syncedConfig(10, 10, 2);

        assertNotNull(config.toString());
    }

    private static Config directConfig(
            int populationSize,
            int tickCount,
            int threadCount,
            Duration threadTimeout,
            DefaultAgentGenerator agentGenerator,
            EnvironmentGenerator environmentGenerator,
            Scheduler scheduler,
            AttributeSetLogDatabaseFactory databaseFactory
    ) {
        return new Config(
                populationSize,
                tickCount,
                threadCount,
                threadTimeout,
                true,
                agentGenerator,
                environmentGenerator,
                scheduler,
                databaseFactory,
                1L
        );
    }
}
