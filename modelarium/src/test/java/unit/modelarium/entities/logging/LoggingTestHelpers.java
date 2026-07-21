package unit.modelarium.entities.logging;

import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.AttributeBase;
import modelarium.entities.attributes.events.functional.FunctionalAgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.databases.factories.MemoryBasedAttributeSetLogDatabaseFactory;

import java.util.List;

class LoggingTestHelpers {
    private LoggingTestHelpers() {}

    static class AgentCounterProperty extends AgentProperty<Double> {
        private double value = 0.0;

        AgentCounterProperty(String name, boolean isLogged) {
            super(name, isLogged, AttributeAccessLevel.PUBLIC, Double.class);
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

    static AgentCounterProperty loggedProperty(String name) {
        return new AgentCounterProperty(name, true);
    }

    static AgentCounterProperty unloggedProperty(String name) {
        return new AgentCounterProperty(name, false);
    }

    static FunctionalAgentEvent loggedEvent(String name) {
        return new FunctionalAgentEvent(
                name,
                true,
                AttributeAccessLevel.PUBLIC,
                (context) -> {},
                (context) -> true
        );
    }

    @SuppressWarnings("unchecked")
    static AttributeSetLog<AgentSimulationContext> attributeSetLog(
            String ownerName,
            String attributeSetName,
            Attribute... attributes
    ) {
        return new AttributeSetLog<>(
                ownerName,
                attributeSetName,
                new MemoryBasedAttributeSetLogDatabaseFactory(),
                (List<AttributeBase<AgentSimulationContext>>) (List<?>) List.of(attributes)
        );
    }

    @SuppressWarnings("unchecked")
    static AgentAttributeSet agentAttributeSetWithMemoryLog(
            String ownerName,
            String attributeSetName,
            Attribute... attributes
    ) {
        AgentAttributeSet attributeSet = new AgentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(attributes)
        );
        attributeSet.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());
        return attributeSet;
    }
}
