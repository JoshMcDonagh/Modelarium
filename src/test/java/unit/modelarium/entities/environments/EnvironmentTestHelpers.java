package unit.modelarium.entities.environments;

import modelarium.Config;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.EnvironmentGenerator;

import java.util.List;

class EnvironmentTestHelpers {
    private EnvironmentTestHelpers() {}

    static Environment emptyEnvironment() {
        return new Environment("env", List.of());
    }

    static class EnvironmentTickProperty extends EnvironmentProperty<Integer> {
        private int tick = 0;

        EnvironmentTickProperty(String name) {
            super(name, true, AttributeAccessLevel.PUBLIC, Integer.class);
        }

        @Override
        protected void run(EnvironmentContext context) {
            tick++;
        }

        @Override
        protected void set(EnvironmentContext context, Integer value) {
            this.tick = value;
        }

        @Override
        protected Integer get(EnvironmentContext context) {
            return tick;
        }
    }

    @SuppressWarnings("unchecked")
    static EnvironmentAttributeSet environmentAttributeSet(String ownerName, String attributeSetName, EnvironmentProperty<?>... properties) {
        return new EnvironmentAttributeSet(
                ownerName,
                attributeSetName,
                (List<Attribute<EnvironmentSimulationContext>>) (List<?>) List.of(properties)
        );
    }

    static EnvironmentAttributeSet emptyEnvironmentAttributeSet(String ownerName, String attributeSetName) {
        return new EnvironmentAttributeSet(ownerName, attributeSetName, List.of());
    }

    static DefaultAgentGenerator agentGenerator() {
        return new DefaultAgentGenerator() {
            private int index = 0;

            @Override
            protected Agent generateAgent(Config config) {
                return new Agent("agent_" + index++, List.of());
            }
        };
    }

    static EnvironmentGenerator environmentGenerator() {
        return new EnvironmentGenerator() {
            @Override
            public Environment generateEnvironment(Config config) {
                return emptyEnvironment();
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
