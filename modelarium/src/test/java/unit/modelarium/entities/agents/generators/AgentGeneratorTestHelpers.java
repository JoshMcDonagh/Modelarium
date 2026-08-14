package unit.modelarium.entities.agents.generators;

import modelarium.Config;
import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.environments.MutableEnvironment;
import modelarium.entities.environments.generators.EnvironmentGenerator;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;

class AgentGeneratorTestHelpers {
    private AgentGeneratorTestHelpers() {}

    private static final AtomicInteger AGENT_COUNTER = new AtomicInteger(0);

    static MutableAgent uniqueAgent() {
        return new MutableAgent("agent_" + AGENT_COUNTER.getAndIncrement(), List.of());
    }

    static DefaultAgentGenerator agentGenerator() {
        return new DefaultAgentGenerator() {
            @Override
            protected MutableAgent generateAgent(Config config, RandomGenerator random) {
                return uniqueAgent();
            }
        };
    }

    static EnvironmentGenerator environmentGenerator() {
        return new EnvironmentGenerator() {
            @Override
            public MutableEnvironment generateEnvironment(Config config, RandomGenerator random) {
                return new MutableEnvironment("env", List.of());
            }
        };
    }

    static Config syncedConfig(int populationSize, int tickCount, int threadCount) {
        return Config.builder()
                .populationSize(populationSize)
                .tickCount(tickCount)
                .threadCount(threadCount)
                .areThreadsSynced(true)
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .build();
    }
}
