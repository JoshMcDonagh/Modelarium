package unit.modelarium.entities.immutable;

import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.attributes.*;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.attributes.sets.mutable.MutableEnvironmentAttributeSet;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.environments.MutableEnvironment;

import java.util.List;

class ImmutableEntityTestHelpers {
    private ImmutableEntityTestHelpers() {}

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

    static MutableAgent agentWithCounter(String name) {
        return new MutableAgent(name, List.of(singlePropertyAgentSet(name, "stats", "counter")));
    }

    @SuppressWarnings("unchecked")
    static MutableEnvironment environmentWithTickCounter() {
        MutableEnvironmentAttributeSet attributeSet = new MutableEnvironmentAttributeSet(
                "timing",
                (List<Attribute>) (List<?>) List.of(new EnvironmentTickProperty("envTick"))
        );
        return new MutableEnvironment(List.of(attributeSet));
    }
}
