package unit.modelarium.entities.attributes;

import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;

import java.util.List;

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
                ownerName,
                attributeSetName,
                (List<Attribute<AgentSimulationContext>>) (List<?>) List.of(properties)
        );
    }

    @SuppressWarnings("unchecked")
    static AgentAttributeSet agentAttributeSetFromEvents(String ownerName, String attributeSetName, AgentEvent... events) {
        return new AgentAttributeSet(
                ownerName,
                attributeSetName,
                (List<Attribute<AgentSimulationContext>>) (List<?>) List.of(events)
        );
    }

    @SuppressWarnings("unchecked")
    static AgentAttributeSet agentAttributeSetFromRoutines(String ownerName, String attributeSetName, AgentRoutine... routines) {
        return new AgentAttributeSet(
                ownerName,
                attributeSetName,
                (List<Attribute<AgentSimulationContext>>) (List<?>) List.of(routines)
        );
    }

    static AgentAttributeSet singlePropertyAgentSet(String ownerName, String attributeSetName, String propertyName) {
        return agentAttributeSet(ownerName, attributeSetName, new AgentCounterProperty(propertyName));
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
}
