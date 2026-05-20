package modelarium.entities.immutable.attributes;

import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.attributes.events.Event;
import modelarium.entities.attributes.properties.Property;
import modelarium.entities.attributes.routines.Routine;
import modelarium.entities.contexts.Context;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.logging.AttributeSetLog;

public sealed abstract class ImmutableAttributeSet<SC extends SimulationContext, C extends Context> permits ImmutableAgentAttributeSet, ImmutableEnvironmentAttributeSet {
    private final AttributeSet<SC,C> attributeSet;

    protected ImmutableAttributeSet(AttributeSet<SC,C> attributeSet) {
        this.attributeSet = attributeSet;
    }

    AttributeSet<SC,C> getMutableAttributeSet() {
        return attributeSet;
    }

    public String name() {
        return attributeSet.name();
    }

    public int size() {
        return attributeSet.size();
    }

    public AttributeSetLog<SC> getLog() {
        return attributeSet.getLog();
    }

    public abstract Event<C> getEvent(int eventIndex);

    public abstract Event<C> getEvent(String eventName);

    public abstract Routine<C> getRoutine(int routineIndex);

    public abstract Routine<C> getRoutine(String routineName);

    public abstract Property<?,C> getProperty(int propertyIndex);

    public abstract Property<?,C> getProperty(String propertyName);
}
