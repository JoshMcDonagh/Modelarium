package modelarium.entities.attributes;

import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.contexts.AgentContext;

import java.util.List;

public class AgentAttributeSet extends AttributeSet<AgentContext> {
    public AgentAttributeSet(String ownerName, String attributeSetName, List<Attribute<AgentContext>> attributes) {
        super(ownerName, attributeSetName, attributes);
    }

    public AgentEvent getEvent(int eventIndex) {
        return (AgentEvent) super.getEvent(eventIndex);
    }

    public AgentEvent getEvent(String eventName) {
        return (AgentEvent) super.getEvent(eventName);
    }

    public AgentRoutine getRoutine(int routineIndex) {
        return (AgentRoutine) super.getRoutine(routineIndex);
    }

    public AgentRoutine getRoutine(String routineName) {
        return (AgentRoutine) super.getRoutine(routineName);
    }

    public AgentProperty<?> getProperty(int propertyIndex) {
        return (AgentProperty<?>) super.getProperty(propertyIndex);
    }

    public AgentProperty<?> getProperty(String propertyName) {
        return (AgentProperty<?>) super.getProperty(propertyName);
    }
}
