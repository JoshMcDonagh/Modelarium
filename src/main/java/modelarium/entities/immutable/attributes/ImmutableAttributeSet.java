package modelarium.entities.immutable.attributes;

import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.events.Event;
import modelarium.entities.attributes.properties.Property;
import modelarium.entities.attributes.routines.Routine;
import modelarium.entities.contexts.Context;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.utils.Cloners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Abstract class for providing a read-only view of an {@link AttributeSet}.
 *
 * <p>This class wraps a mutable attribute set so that its attributes can be inspected without the underlying set
 * being modifiable. Retrieved attributes are deep cloned, so changes made to them do not affect the original set.
 * It is extended by {@link ImmutableAgentAttributeSet} and {@link ImmutableEnvironmentAttributeSet}.
 *
 * @param <SC> the type of simulation context the wrapped attribute set uses
 * @param <C> the type of context interface the wrapped attribute set's attributes are given
 */
public sealed abstract class ImmutableAttributeSet<SC extends SimulationContext, C extends Context> permits ImmutableAgentAttributeSet, ImmutableEnvironmentAttributeSet {

    /** The mutable attribute set this read-only view wraps */
    private final AttributeSet<SC,C> attributeSet;

    /**
     * Constructs a new immutable attribute set wrapping the specified mutable attribute set.
     *
     * @param attributeSet the mutable attribute set to provide a read-only view of
     */
    protected ImmutableAttributeSet(AttributeSet<SC,C> attributeSet) {
        this.attributeSet = attributeSet;
    }

    /**
     * Retrieves an attribute from the wrapped set reflectively and returns a deep clone of it.
     *
     * <p>The wrapped set's typed getters are package-private, so this method invokes the named getter reflectively
     * on the concrete attribute set class and deep clones the result before returning it.
     *
     * @param attributeSetClass the concrete class of the wrapped attribute set
     * @param attributeReturnClass the class the retrieved attribute is returned as
     * @param getterMethodName the name of the getter method to invoke on the wrapped set
     * @param attributeIdClass the class of the identifier the getter takes (an index or a name)
     * @param attributeId the identifier of the attribute to retrieve
     * @param <A> the type of the wrapped attribute set
     * @param <T> the type the retrieved attribute is returned as
     * @param <P> the type of the identifier the getter takes
     * @return a deep clone of the retrieved attribute
     */
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

    /**
     * Returns the name of the wrapped attribute set.
     *
     * @return the attribute set's name
     */
    public String name() {
        return attributeSet.name();
    }

    /**
     * Returns the number of attributes in the wrapped set.
     *
     * @return the attribute set's size
     */
    public int size() {
        return attributeSet.size();
    }

    /**
     * Returns the log recording the values of the wrapped set's logged attributes.
     *
     * @return the attribute set's {@link AttributeSetLog} instance
     */
    public AttributeSetLog<SC> getLog() {
        return attributeSet.getLog();
    }

    /**
     * Retrieves a deep clone of a publicly accessible attribute by index. Must be implemented by subclasses.
     *
     * @param index the index of the attribute to retrieve
     * @return a deep clone of the attribute at the specified index
     */
    public abstract Attribute get(int index);

    /**
     * Retrieves a deep clone of a publicly accessible attribute by name. Must be implemented by subclasses.
     *
     * @param attributeName the name of the attribute to retrieve
     * @return a deep clone of the attribute with the specified name
     */
    public abstract Attribute get(String attributeName);

    /**
     * Retrieves a deep clone of an event by its index among the wrapped set's events. Must be implemented by
     * subclasses.
     *
     * @param eventIndex the index of the event to retrieve
     * @return a deep clone of the event at the specified index
     */
    public abstract Event<C> getEvent(int eventIndex);

    /**
     * Retrieves a deep clone of an event by name. Must be implemented by subclasses.
     *
     * @param eventName the name of the event to retrieve
     * @return a deep clone of the event with the specified name
     */
    public abstract Event<C> getEvent(String eventName);

    /**
     * Retrieves a deep clone of a routine by its index among the wrapped set's routines. Must be implemented by
     * subclasses.
     *
     * @param routineIndex the index of the routine to retrieve
     * @return a deep clone of the routine at the specified index
     */
    public abstract Routine<C> getRoutine(int routineIndex);

    /**
     * Retrieves a deep clone of a routine by name. Must be implemented by subclasses.
     *
     * @param routineName the name of the routine to retrieve
     * @return a deep clone of the routine with the specified name
     */
    public abstract Routine<C> getRoutine(String routineName);

    /**
     * Retrieves a deep clone of a property by its index among the wrapped set's properties. Must be implemented by
     * subclasses.
     *
     * @param propertyIndex the index of the property to retrieve
     * @return a deep clone of the property at the specified index
     */
    public abstract Property<?,C> getProperty(int propertyIndex);

    /**
     * Retrieves a deep clone of a property by name. Must be implemented by subclasses.
     *
     * @param propertyName the name of the property to retrieve
     * @return a deep clone of the property with the specified name
     */
    public abstract Property<?,C> getProperty(String propertyName);
}
