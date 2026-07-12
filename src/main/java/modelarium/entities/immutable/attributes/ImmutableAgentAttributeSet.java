package modelarium.entities.immutable.attributes;

import modelarium.entities.attributes.AgentAttribute;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;

public final class ImmutableAgentAttributeSet extends ImmutableAttributeSet<AgentSimulationContext, AgentContext> {
    public ImmutableAgentAttributeSet(AgentAttributeSet attributeSet) {
        super(attributeSet);
    }

    @Override
    public AgentAttribute get(int index) {
        return getClonedAttribute(
                AgentAttributeSet.class,
                AgentAttribute.class,
                "get",
                int.class,
                index
        );
    }

    @Override
    public AgentAttribute get(String attributeName) {
        return getClonedAttribute(
                AgentAttributeSet.class,
                AgentAttribute.class,
                "get",
                String.class,
                attributeName
        );
    }

    @Override
    public AgentEvent getEvent(int eventIndex) {
        return getClonedAttribute(
                AgentAttributeSet.class,
                AgentEvent.class,
                "getEvent",
                int.class,
                eventIndex
        );
    }

    @Override
    public AgentEvent getEvent(String eventName) {
        return getClonedAttribute(
                AgentAttributeSet.class,
                AgentEvent.class,
                "getEvent",
                String.class,
                eventName
        );
    }

    @Override
    public AgentRoutine getRoutine(int routineIndex) {
        return getClonedAttribute(
                AgentAttributeSet.class,
                AgentRoutine.class,
                "getRoutine",
                int.class,
                routineIndex
        );
    }

    @Override
    public AgentRoutine getRoutine(String routineName) {
        return getClonedAttribute(
                AgentAttributeSet.class,
                AgentRoutine.class,
                "getRoutine",
                String.class,
                routineName
        );
    }

    @Override
    public AgentProperty<?> getProperty(int propertyIndex) {
        return getClonedAttribute(
                AgentAttributeSet.class,
                AgentProperty.class,
                "getProperty",
                int.class,
                propertyIndex
        );
    }

    @Override
    public AgentProperty<?> getProperty(String propertyName) {
        return getClonedAttribute(
                AgentAttributeSet.class,
                AgentProperty.class,
                "getProperty",
                String.class,
                propertyName
        );
    }
}
