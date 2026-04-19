package modelarium.entities.attributes;

import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;

import java.util.List;

public class EnvironmentAttributeSet extends AttributeSet<EnvironmentSimulationContext, EnvironmentContext> {
    public EnvironmentAttributeSet(String ownerName, String attributeSetName, List<Attribute<EnvironmentSimulationContext>> attributes) {
        super(ownerName, attributeSetName, attributes);
    }

    public EnvironmentEvent getEvent(int eventIndex) {
        return (EnvironmentEvent) super.getEvent(eventIndex);
    }

    public EnvironmentEvent getEvent(String eventName) {
        return (EnvironmentEvent) super.getEvent(eventName);
    }

    public EnvironmentRoutine getRoutine(int routineIndex) {
        return (EnvironmentRoutine) super.getRoutine(routineIndex);
    }

    public EnvironmentRoutine getRoutine(String routineName) {
        return (EnvironmentRoutine) super.getRoutine(routineName);
    }

    public EnvironmentProperty<?> getProperty(int propertyIndex) {
        return (EnvironmentProperty<?>) super.getProperty(propertyIndex);
    }

    public EnvironmentProperty<?> getProperty(String propertyName) {
        return (EnvironmentProperty<?>) super.getProperty(propertyName);
    }
}
