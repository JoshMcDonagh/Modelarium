package modelarium.entities.immutable.attributes;

import com.rits.cloning.Cloner;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;

public final class ImmutableEnvironmentAttributeSet extends ImmutableAttributeSet<EnvironmentSimulationContext, EnvironmentContext> {
    private static final Cloner cloner = new Cloner();

    public ImmutableEnvironmentAttributeSet(EnvironmentAttributeSet attributeSet) {
        super(attributeSet);
    }

    @Override
    public EnvironmentEvent getEvent(int eventIndex) {
        return cloner.deepClone(((EnvironmentAttributeSet) getMutableAttributeSet()).getEvent(eventIndex));
    }

    @Override
    public EnvironmentEvent getEvent(String eventName) {
        return cloner.deepClone(((EnvironmentAttributeSet) getMutableAttributeSet()).getEvent(eventName));
    }

    @Override
    public EnvironmentRoutine getRoutine(int routineIndex) {
        return cloner.deepClone(((EnvironmentAttributeSet) getMutableAttributeSet()).getRoutine(routineIndex));
    }

    @Override
    public EnvironmentRoutine getRoutine(String routineName) {
        return cloner.deepClone(((EnvironmentAttributeSet) getMutableAttributeSet()).getRoutine(routineName));
    }

    @Override
    public EnvironmentProperty<?> getProperty(int propertyIndex) {
        return cloner.deepClone(((EnvironmentAttributeSet) getMutableAttributeSet()).getProperty(propertyIndex));
    }

    @Override
    public EnvironmentProperty<?> getProperty(String propertyName) {
        return cloner.deepClone(((EnvironmentAttributeSet) getMutableAttributeSet()).getProperty(propertyName));
    }
}
