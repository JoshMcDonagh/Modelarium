package modelarium.entities.agents;

import modelarium.entities.Entity;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.logging.AttributeSetLog;

import java.util.List;

public class Agent extends Entity<AgentContext, AgentAttributeSet, AttributeSetLog<AgentContext>> {
    public Agent(String name, List<AgentAttributeSet> attributeSets) {
        super(name, attributeSets);
    }

    @Override
    public Agent clone() {
        return getCloner().deepClone(this);
    }

    public AgentEvent getEvent(String attributeSetName, String eventName) {
        return getAttributeSet(attributeSetName).getEvent(eventName);
    }

    public AgentRoutine getRoutine(String attributeSetName, String routineName) {
        return getAttributeSet(attributeSetName).getRoutine(routineName);
    }

    public AgentProperty<?> getProperty(String attributeSetName, String propertyName) {
        return getAttributeSet(attributeSetName).getProperty(propertyName);
    }
}
