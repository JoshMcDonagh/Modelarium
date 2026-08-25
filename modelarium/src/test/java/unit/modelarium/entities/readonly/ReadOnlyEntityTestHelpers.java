package unit.modelarium.entities.readonly;

import modelarium.entities.Agent;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.sets.AgentAttributeSet;
import modelarium.entities.attributes.sets.EnvironmentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.Environment;

import java.util.List;

public class ReadOnlyEntityTestHelpers {
    private ReadOnlyEntityTestHelpers() {}

    public static Agent emptyAgent(String name) {
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
    public static AgentAttributeSet singlePropertyAgentSet(String ownerName, String attributeSetName, String propertyName) {
        return new AgentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(new AgentCounterProperty(propertyName))
        );
    }

    public static Agent agentWithCounter(String name) {
        return new Agent(name, List.of(singlePropertyAgentSet(name, "stats", "counter")));
    }

    @SuppressWarnings("unchecked")
    public static Environment environmentWithTickCounter() {
        EnvironmentAttributeSet attributeSet = new EnvironmentAttributeSet(
                "timing",
                (List<Attribute>) (List<?>) List.of(new EnvironmentTickProperty("envTick"))
        );
        return new Environment(List.of(attributeSet));
    }
}
