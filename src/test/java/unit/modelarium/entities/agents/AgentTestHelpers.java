package unit.modelarium.entities.agents;

import modelarium.entities.agents.Agent;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeBase;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;

import java.util.List;

class AgentTestHelpers {
    private AgentTestHelpers() {}

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

    @SuppressWarnings("unchecked")
    static AgentAttributeSet agentAttributeSet(String ownerName, String attributeSetName, AgentProperty<?>... properties) {
        return new AgentAttributeSet(
                ownerName,
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(properties)
        );
    }

    static AgentAttributeSet singlePropertyAgentSet(String ownerName, String attributeSetName, String propertyName) {
        return agentAttributeSet(ownerName, attributeSetName, new AgentCounterProperty(propertyName));
    }
}
