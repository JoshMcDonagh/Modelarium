package unit.modelarium.entities.agents.generators;

import modelarium.Config;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.generators.EnvironmentGenerator;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;

class AgentGeneratorTestHelpers {
    private AgentGeneratorTestHelpers() {}

    private static final AtomicInteger AGENT_COUNTER = new AtomicInteger(0);

    static Agent uniqueAgent() {
        return new Agent("agent_" + AGENT_COUNTER.getAndIncrement(), List.of());
    }

    static DefaultAgentGenerator agentGenerator() {
        return new DefaultAgentGenerator() {
            @Override
            protected Agent generateAgent(Config config, RandomGenerator random) {
                return uniqueAgent();
            }
        };
    }

    static EnvironmentGenerator environmentGenerator() {
        return new EnvironmentGenerator() {
            @Override
            public Environment generateEnvironment(Config config, RandomGenerator random) {
                return new Environment("env", List.of());
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
