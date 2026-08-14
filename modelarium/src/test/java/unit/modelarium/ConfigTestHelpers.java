package unit.modelarium;

import modelarium.Config;
import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.environments.MutableEnvironment;
import modelarium.entities.environments.generators.EnvironmentGenerator;

import java.util.List;
import java.util.random.RandomGenerator;

class ConfigTestHelpers {
    private ConfigTestHelpers() {}

    static DefaultAgentGenerator agentGenerator() {
        return new DefaultAgentGenerator() {
            private int index = 0;

            @Override
            protected MutableAgent generateAgent(Config config, RandomGenerator random) {
                return new MutableAgent("agent_" + index++, List.of());
            }
        };
    }

    static EnvironmentGenerator environmentGenerator() {
        return new EnvironmentGenerator() {
            @Override
            public MutableEnvironment generateEnvironment(Config config, RandomGenerator random) {
                return new MutableEnvironment(List.of());
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
