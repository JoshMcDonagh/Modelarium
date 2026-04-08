package modelarium.entities.environments;

import modelarium.entities.Entity;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.logging.AttributeSetLog;

import java.util.List;

public class Environment extends Entity<EnvironmentContext, EnvironmentAttributeSet, AttributeSetLog<EnvironmentContext>> {
    public Environment(String name, List<EnvironmentAttributeSet> attributeSets) {
        super(name, attributeSets);
    }

    @Override
    public Environment clone() {
        return getCloner().deepClone(this);
    }

    public EnvironmentEvent getEvent(String attributeSetName, String eventName) {
        return getAttributeSet(attributeSetName).getEvent(eventName);
    }

    public EnvironmentRoutine getRoutine(String attributeSetName, String routineName) {
        return getAttributeSet(attributeSetName).getRoutine(routineName);
    }

    public EnvironmentProperty<?> getProperty(String attributeSetName, String propertyName) {
        return getAttributeSet(attributeSetName).getProperty(propertyName);
    }
}
