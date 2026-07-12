package modelarium.entities.immutable.attributes;

import modelarium.entities.attributes.*;
import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;

public final class ImmutableEnvironmentAttributeSet extends ImmutableAttributeSet<EnvironmentSimulationContext, EnvironmentContext> {
    public ImmutableEnvironmentAttributeSet(EnvironmentAttributeSet attributeSet) {
        super(attributeSet);
    }

    @Override
    public EnvironmentAttribute get(int index) {
        return getClonedAttribute(
                EnvironmentAttributeSet.class,
                EnvironmentAttribute.class,
                "get",
                int.class,
                index
        );
    }

    @Override
    public EnvironmentAttribute get(String attributeName) {
        return getClonedAttribute(
                EnvironmentAttributeSet.class,
                EnvironmentAttribute.class,
                "get",
                String.class,
                attributeName
        );
    }

    @Override
    public EnvironmentEvent getEvent(int eventIndex) {
        return getClonedAttribute(
                EnvironmentAttributeSet.class,
                EnvironmentEvent.class,
                "getEvent",
                int.class,
                eventIndex
        );
    }

    @Override
    public EnvironmentEvent getEvent(String eventName) {
        return getClonedAttribute(
                EnvironmentAttributeSet.class,
                EnvironmentEvent.class,
                "getEvent",
                String.class,
                eventName
        );
    }

    @Override
    public EnvironmentRoutine getRoutine(int routineIndex) {
        return getClonedAttribute(
                EnvironmentAttributeSet.class,
                EnvironmentRoutine.class,
                "getRoutine",
                int.class,
                routineIndex
        );
    }

    @Override
    public EnvironmentRoutine getRoutine(String routineName) {
        return getClonedAttribute(
                EnvironmentAttributeSet.class,
                EnvironmentRoutine.class,
                "getRoutine",
                String.class,
                routineName
        );
    }

    @Override
    public EnvironmentProperty<?> getProperty(int propertyIndex) {
        return getClonedAttribute(
                EnvironmentAttributeSet.class,
                EnvironmentProperty.class,
                "getProperty",
                int.class,
                propertyIndex
        );
    }

    @Override
    public EnvironmentProperty<?> getProperty(String propertyName) {
        return getClonedAttribute(
                EnvironmentAttributeSet.class,
                EnvironmentProperty.class,
                "getProperty",
                String.class,
                propertyName
        );
    }
}
