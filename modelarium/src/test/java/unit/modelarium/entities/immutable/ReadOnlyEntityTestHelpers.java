package unit.modelarium.entities.immutable;

import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.attributes.sets.mutable.MutableEnvironmentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.environments.Environment;

import java.util.List;

class ReadOnlyEntityTestHelpers {
    private ReadOnlyEntityTestHelpers() {}

    static Agent emptyAgent(String name) {
        return new Agent(name, List.of());
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
    static MutableAgentAttributeSet singlePropertyAgentSet(String ownerName, String attributeSetName, String propertyName) {
        return new MutableAgentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(new AgentCounterProperty(propertyName))
        );
    }

    static Agent agentWithCounter(String name) {
        return new Agent(name, List.of(singlePropertyAgentSet(name, "stats", "counter")));
    }

    @SuppressWarnings("unchecked")
    static Environment environmentWithTickCounter() {
        MutableEnvironmentAttributeSet attributeSet = new MutableEnvironmentAttributeSet(
                "timing",
                (List<Attribute>) (List<?>) List.of(new EnvironmentTickProperty("envTick"))
        );
        return new Environment(List.of(attributeSet));
    }
}
