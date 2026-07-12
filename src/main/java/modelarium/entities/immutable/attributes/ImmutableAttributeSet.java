package modelarium.entities.immutable.attributes;

import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.attributes.EntityAttribute;
import modelarium.entities.attributes.events.Event;
import modelarium.entities.attributes.properties.Property;
import modelarium.entities.attributes.routines.Routine;
import modelarium.entities.contexts.Context;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.utils.Cloners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public sealed abstract class ImmutableAttributeSet<SC extends SimulationContext, C extends Context> permits ImmutableAgentAttributeSet, ImmutableEnvironmentAttributeSet {
    private final AttributeSet<SC,C> attributeSet;

    protected ImmutableAttributeSet(AttributeSet<SC,C> attributeSet) {
        this.attributeSet = attributeSet;
    }

    <A, T, P> T getClonedAttribute(
            Class<A> attributeSetClass,
            Class<T> attributeReturnClass,
            String getterMethodName,
            Class<P> attributeIdClass,
            P attributeId
    ) {
        A mutableAttributeSet = attributeSetClass.cast(attributeSet);
        Method getterMethod;
        try {
            getterMethod = mutableAttributeSet.getClass().getDeclaredMethod(
                    getterMethodName,
                    attributeIdClass
            );
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Method '" + getterMethodName + "' taking a '" + attributeIdClass.getName() + "' not found", e);
        }

        try {
            return Cloners.standard().deepClone(attributeReturnClass.cast(getterMethod.invoke(mutableAttributeSet, attributeId)));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Method '" + getterMethodName + "' could not be invoked with '" + attributeId.toString() + "'", e);
        }
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

    public abstract EntityAttribute get(int index);

    public abstract EntityAttribute get(String attributeName);

    public abstract Event<C> getEvent(int eventIndex);

    public abstract Event<C> getEvent(String eventName);

    public abstract Routine<C> getRoutine(int routineIndex);

    public abstract Routine<C> getRoutine(String routineName);

    public abstract Property<?,C> getProperty(int propertyIndex);

    public abstract Property<?,C> getProperty(String propertyName);
}
