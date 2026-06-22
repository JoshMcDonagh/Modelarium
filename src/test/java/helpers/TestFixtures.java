package helpers;

import modelarium.Config;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.EnvironmentGenerator;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Factories for constructing test-ready agents, environments, and configs.
 */
public final class TestFixtures {

    private TestFixtures() {}

    private static final AtomicInteger AGENT_COUNTER = new AtomicInteger(0);

    /**
     * Creates a minimal agent with a single counter property.
     */
    public static Agent agentWithCounter(String name) {
        AgentAttributeSet attrSet = TestAttributes.singlePropertyAgentSet(name, "stats", "counter");
        return new Agent(name, List.of(attrSet));
    }

    /**
     * Creates a bare agent with no attributes.
     */
    public static Agent emptyAgent(String name) {
        return new Agent(name, List.of());
    }

    /**
     * Creates an agent with a unique auto-generated name.
     */
    public static Agent uniqueAgent() {
        return agentWithCounter("agent_" + AGENT_COUNTER.getAndIncrement());
    }

    /**
     * Creates a minimal environment with a single tick-counting property.
     */
    public static Environment environmentWithTickCounter() {
        EnvironmentAttributeSet attrSet = TestAttributes.environmentAttributeSet(
                "env", "timing", new TestAttributes.EnvironmentTickProperty("envTick"));
        return new Environment("env", List.of(attrSet));
    }

    /**
     * Creates a bare environment with no attributes.
     */
    public static Environment emptyEnvironment() {
        return new Environment("env", List.of());
    }

    /**
     * A default agent generator that produces agents with counter properties.
     */
    public static DefaultAgentGenerator counterAgentGenerator() {
        return new DefaultAgentGenerator() {
            private int index = 0;

            @Override
            protected Agent generateAgent(Config config) {
                return agentWithCounter("agent_" + index++);
            }
        };
    }

    /**
     * An environment generator that produces a minimal environment.
     */
    public static EnvironmentGenerator simpleEnvironmentGenerator() {
        return new EnvironmentGenerator() {
            @Override
            public Environment generateEnvironment(Config config) {
                return environmentWithTickCounter();
            }
        };
    }

    /**
     * An environment generator that produces a bare environment.
     */
    public static EnvironmentGenerator emptyEnvironmentGenerator() {
        return new EnvironmentGenerator() {
            @Override
            public Environment generateEnvironment(Config config) {
                return emptyEnvironment();
            }
        };
    }

    /**
     * Builds a standard synced config for integration tests.
     */
    public static Config syncedConfig(int population, int ticks, int threads) {
        return Config.builder()
                .populationSize(population)
                .tickCount(ticks)
                .threadCount(threads)
                .areThreadsSynced(true)
                .agentGenerator(counterAgentGenerator())
                .environmentGenerator(simpleEnvironmentGenerator())
                .build();
    }

    /**
     * Builds a standard unsynced config for integration tests.
     */
    public static Config unsyncedConfig(int population, int ticks, int threads) {
        return Config.builder()
                .populationSize(population)
                .tickCount(ticks)
                .threadCount(threads)
                .areThreadsSynced(false)
                .agentGenerator(counterAgentGenerator())
                .environmentGenerator(simpleEnvironmentGenerator())
                .build();
    }
}
