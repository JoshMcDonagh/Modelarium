package unit.modelarium.multithreading.requestresponse;

import modelarium.Config;
import modelarium.entities.generators.DefaultAgentGenerator;
import modelarium.entities.Agent;
import modelarium.entities.Environment;
import modelarium.entities.generators.EnvironmentGenerator;
import modelarium.multithreading.requestresponse.RequestResponseController;

import java.util.List;
import java.util.random.RandomGenerator;

class RequestResponseTestHelpers {
    private RequestResponseTestHelpers() {}

    private static DefaultAgentGenerator agentGenerator() {
        return new DefaultAgentGenerator() {
            private int index = 0;

            @Override
            protected Agent generateAgent(Config config, RandomGenerator random) {
                return new Agent("agent_" + index++, List.of());
            }
        };
    }

    private static EnvironmentGenerator environmentGenerator() {
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

    static RequestResponseController requestResponseController() {
        return new RequestResponseController(syncedConfig(2, 5, 2));
    }
}
