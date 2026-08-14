package unit.modelarium.entities.agents;

import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;

import java.util.List;
import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.mutable.MutableAgentSet;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.MutableEnvironment;
import modelarium.entities.environments.generators.EnvironmentGenerator;
import modelarium.multithreading.requestresponse.RequestResponseController;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

class AgentTestHelpers {
    private AgentTestHelpers() {}

    static MutableAgent emptyAgent(String name) {
        return new MutableAgent(name, List.of());
    }

    static class AgentCounterProperty extends AgentProperty<Double> {
        private double value = 0.0;

        AgentCounterProperty(String name) {
            super(name, true, AttributeAccessLevel.PUBLIC, Double.class);
        }

        @Override
        protected void run(AgentContext context) {
            value += 1.0;
        }

        @Override
        protected void set(AgentContext context, Double value) {
            this.value = value;
        }

        @Override
        protected Double get(AgentContext context) {
            return value;
        }
    }

    @SuppressWarnings("unchecked")
    static MutableAgentAttributeSet agentAttributeSet(String ownerName, String attributeSetName, AgentProperty<?>... properties) {
        return new MutableAgentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(properties)
        );
    }

    static MutableAgentAttributeSet singlePropertyAgentSet(String ownerName, String attributeSetName, String propertyName) {
        return agentAttributeSet(ownerName, attributeSetName, new AgentCounterProperty(propertyName));
    }


    static class AlwaysTriggeredAgentEvent extends AgentEvent {
        private int runCount = 0;

        AlwaysTriggeredAgentEvent(String name) {
            super(name, true, AttributeAccessLevel.PUBLIC);
        }

        @Override
        protected boolean isTriggered(AgentContext context) {
            return true;
        }

        @Override
        protected void run(AgentContext context) {
            runCount++;
        }

        int runCount() {
            return runCount;
        }
    }

    static class EmptyAgentRoutine extends AgentRoutine {
        EmptyAgentRoutine(String name) {
            super(name, AttributeAccessLevel.PUBLIC);
        }

        @Override
        protected void run(AgentContext context) {}
    }

    @SuppressWarnings("unchecked")
    static MutableAgentAttributeSet agentAttributeSetFromAttributes(String ownerName, String attributeSetName, Attribute... attributes) {
        return new MutableAgentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(attributes)
        );
    }

    private static DefaultAgentGenerator agentGenerator() {
        return new DefaultAgentGenerator() {
            private int index = 0;

            @Override
            protected MutableAgent generateAgent(Config config, RandomGenerator random) {
                return new MutableAgent("agent_" + index++, List.of());
            }
        };
    }

    private static EnvironmentGenerator environmentGenerator() {
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

    static void createContextFor(MutableAgent agent) {
        Config config = syncedConfig(1, 1, 1);
        agent.createContext(
                new MutableAgentSet(List.of(agent)),
                config,
                new ContextCache(),
                new MutableClock(config.tickCount()),
                new RequestResponseController(config),
                new MutableEnvironment("env", List.of()),
                new SplittableRandom()
        );
    }
}
