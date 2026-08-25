package unit.modelarium.entities.attributes.sets;

import modelarium.Config;
import modelarium.clock.Clock;
import modelarium.entities.generators.DefaultAgentGenerator;
import modelarium.entities.Agent;
import modelarium.entities.agentsets.AgentSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.attributes.sets.AgentAttributeSet;
import modelarium.entities.attributes.sets.EnvironmentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.Environment;
import modelarium.entities.generators.EnvironmentGenerator;
import modelarium.multithreading.requestresponse.RequestResponseController;

import java.util.List;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

class AttributeTestHelpers {
    private AttributeTestHelpers() {}

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

    static class PrivateCounterProperty extends AgentProperty<Double> {
        private double value = 0.0;

        PrivateCounterProperty(String name) {
            super(name, true, AttributeAccessLevel.PRIVATE, Double.class);
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

    static class AlwaysTriggeredAgentEvent extends AgentEvent {
        AlwaysTriggeredAgentEvent(String name) {
            super(name, true, AttributeAccessLevel.PUBLIC);
        }

        @Override
        protected boolean isTriggered(AgentContext context) {
            return true;
        }

        @Override
        protected void run(AgentContext context) {}
    }

    static class EmptyAgentRoutine extends AgentRoutine {
        EmptyAgentRoutine(String name) {
            super(name, AttributeAccessLevel.PUBLIC);
        }

        @Override
        protected void run(AgentContext context) {}
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
    static AgentAttributeSet agentAttributeSet(String ownerName, String attributeSetName, AgentProperty<?>... properties) {
        return new AgentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(properties)
        );
    }

    @SuppressWarnings("unchecked")
    static AgentAttributeSet agentAttributeSetFromEvents(String ownerName, String attributeSetName, AgentEvent... events) {
        return new AgentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(events)
        );
    }

    @SuppressWarnings("unchecked")
    static AgentAttributeSet agentAttributeSetFromRoutines(String ownerName, String attributeSetName, AgentRoutine... routines) {
        return new AgentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(routines)
        );
    }

    static AgentAttributeSet singlePropertyAgentSet(String ownerName, String attributeSetName, String propertyName) {
        return agentAttributeSet(ownerName, attributeSetName, new AgentCounterProperty(propertyName));
    }

    @SuppressWarnings("unchecked")
    static EnvironmentAttributeSet environmentAttributeSet(String ownerName, String attributeSetName, EnvironmentProperty<?>... properties) {
        return new EnvironmentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(properties)
        );
    }

    static EnvironmentAttributeSet emptyEnvironmentAttributeSet(String ownerName, String attributeSetName) {
        return new EnvironmentAttributeSet(attributeSetName, List.of());
    }


    static class UnloggedAgentCounterProperty extends AgentProperty<Double> {
        private double value = 0.0;

        UnloggedAgentCounterProperty(String name) {
            super(name, false, AttributeAccessLevel.PUBLIC, Double.class);
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

    static class ToggleableAgentEvent extends AgentEvent {
        private final boolean triggered;
        private int runCount = 0;

        ToggleableAgentEvent(String name, boolean triggered) {
            super(name, true, AttributeAccessLevel.PUBLIC);
            this.triggered = triggered;
        }

        @Override
        protected boolean isTriggered(AgentContext context) {
            return triggered;
        }

        @Override
        protected void run(AgentContext context) {
            runCount++;
        }

        int runCount() {
            return runCount;
        }
    }

    static class CountingAgentRoutine extends AgentRoutine {
        private int runCount = 0;

        CountingAgentRoutine(String name) {
            super(name, AttributeAccessLevel.PUBLIC);
        }

        @Override
        protected void run(AgentContext context) {
            runCount++;
        }

        int runCount() {
            return runCount;
        }
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

    static AgentSimulationContext agentSimulationContext(AgentAttributeSet attributeSet) {
        Config config = Config.builder()
                .populationSize(1)
                .tickCount(1)
                .threadCount(1)
                .areThreadsSynced(true)
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .build();

        Agent agent = new Agent("TestOwner", List.of(attributeSet));

        return new AgentSimulationContext(
                agent,
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
