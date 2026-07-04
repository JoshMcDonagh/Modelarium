package modelarium.entities.immutable.attributes;

import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.utils.Cloners;

public final class ImmutableEnvironmentAttributeSet extends ImmutableAttributeSet<EnvironmentSimulationContext, EnvironmentContext> {
    public ImmutableEnvironmentAttributeSet(EnvironmentAttributeSet attributeSet) {
        super(attributeSet);
    }

    @Override
    public EnvironmentEvent getEvent(int eventIndex) {
        return Cloners.standard().deepClone(((EnvironmentAttributeSet) getMutableAttributeSet()).getEvent(eventIndex));
    }

    @Override
    public EnvironmentEvent getEvent(String eventName) {
        return Cloners.standard().deepClone(((EnvironmentAttributeSet) getMutableAttributeSet()).getEvent(eventName));
    }

    @Override
    public EnvironmentRoutine getRoutine(int routineIndex) {
        return Cloners.standard().deepClone(((EnvironmentAttributeSet) getMutableAttributeSet()).getRoutine(routineIndex));
    }

    @Override
    public EnvironmentRoutine getRoutine(String routineName) {
        return Cloners.standard().deepClone(((EnvironmentAttributeSet) getMutableAttributeSet()).getRoutine(routineName));
    }

    @Override
    public EnvironmentProperty<?> getProperty(int propertyIndex) {
        return Cloners.standard().deepClone(((EnvironmentAttributeSet) getMutableAttributeSet()).getProperty(propertyIndex));
    }

    @Override
    public EnvironmentProperty<?> getProperty(String propertyName) {
        return Cloners.standard().deepClone(((EnvironmentAttributeSet) getMutableAttributeSet()).getProperty(propertyName));
    }
}
