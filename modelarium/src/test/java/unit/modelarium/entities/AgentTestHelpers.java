package unit.modelarium.entities;

import modelarium.Config;
import modelarium.clock.Clock;
import modelarium.entities.generators.DefaultAgentGenerator;
import modelarium.entities.Agent;
import modelarium.entities.agentsets.AgentSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.attributes.sets.AgentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.Environment;
import modelarium.entities.generators.EnvironmentGenerator;
import modelarium.multithreading.requestresponse.RequestResponseController;

import java.util.List;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

public class AgentTestHelpers {
    private AgentTestHelpers() {}

    public static Agent emptyAgent(String name) {
        return new Agent(name, List.of());
    }

    public static class AgentCounterProperty extends AgentProperty<Double> {
        private double value = 0.0;

        public AgentCounterProperty(String name) {
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
    public static AgentAttributeSet agentAttributeSet(String ownerName, String attributeSetName, AgentProperty<?>... properties) {
        return new AgentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(properties)
        );
    }

    public static AgentAttributeSet singlePropertyAgentSet(String ownerName, String attributeSetName, String propertyName) {
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
    static AgentAttributeSet agentAttributeSetFromAttributes(String ownerName, String attributeSetName, Attribute... attributes) {
        return new AgentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(attributes)
        );
    }

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

    static void createContextFor(Agent agent) {
        Config config = syncedConfig(1, 1, 1);
        agent.createContext(
                new AgentSet(List.of(agent)),
                config,
                new ContextCache(),
                new Clock(config.tickCount()),
                new RequestResponseController(config),
                new Environment("env", List.of()),
                new SplittableRandom()
        );
    }
}
