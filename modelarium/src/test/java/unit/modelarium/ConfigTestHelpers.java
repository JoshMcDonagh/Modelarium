package unit.modelarium;

import modelarium.Config;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.generators.EnvironmentGenerator;

import java.util.List;
import java.util.random.RandomGenerator;

class ConfigTestHelpers {
    private ConfigTestHelpers() {}

    static DefaultAgentGenerator agentGenerator() {
        return new DefaultAgentGenerator() {
            private int index = 0;

            @Override
            protected Agent generateAgent(Config config, RandomGenerator random) {
                return new Agent("agent_" + index++, List.of());
            }
        };
    }

    static EnvironmentGenerator environmentGenerator() {
        return new EnvironmentGenerator() {
            @Override
            public Environment generateEnvironment(Config config, RandomGenerator random) {
                return new Environment(List.of());
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
