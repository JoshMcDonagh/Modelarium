package modelarium.entities.attributes.sets.immutable;

import modelarium.entities.attributes.EnvironmentAttribute;
import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.attributes.sets.mutable.EnvironmentAttributeSet;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;

/**
 * Class for providing a read-only view of an {@link EnvironmentAttributeSet}.
 *
 * <p>This class specialises {@link ReadOnlyAttributeSet} so that retrieved attributes are returned as deep clones
 * of their environment-flavoured types.
 */
public final class ReadOnlyEnvironmentAttributeSet extends ReadOnlyAttributeSet<EnvironmentSimulationContext, EnvironmentContext> {

    /**
     * Constructs a new immutable environment attribute set wrapping the specified mutable attribute set.
     *
     * @param attributeSet the mutable environment attribute set to provide a read-only view of
     */
    public ReadOnlyEnvironmentAttributeSet(EnvironmentAttributeSet attributeSet) {
        super(attributeSet);
    }

    /**
     * Retrieves a deep clone of a publicly accessible attribute by index.
     *
     * @param index the index of the attribute to retrieve
     * @return a deep clone of the {@link EnvironmentAttribute} at the specified index
     */
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

    /**
     * Retrieves a deep clone of a publicly accessible attribute by name.
     *
     * @param attributeName the name of the attribute to retrieve
     * @return a deep clone of the {@link EnvironmentAttribute} with the specified name
     */
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

    /**
     * Retrieves a deep clone of an event by its index among the wrapped set's events.
     *
     * @param eventIndex the index of the event to retrieve
     * @return a deep clone of the {@link EnvironmentEvent} at the specified index
     */
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

    /**
     * Retrieves a deep clone of an event by name.
     *
     * @param eventName the name of the event to retrieve
     * @return a deep clone of the {@link EnvironmentEvent} with the specified name
     */
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

    /**
     * Retrieves a deep clone of a routine by its index among the wrapped set's routines.
     *
     * @param routineIndex the index of the routine to retrieve
     * @return a deep clone of the {@link EnvironmentRoutine} at the specified index
     */
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

    /**
     * Retrieves a deep clone of a routine by name.
     *
     * @param routineName the name of the routine to retrieve
     * @return a deep clone of the {@link EnvironmentRoutine} with the specified name
     */
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

    /**
     * Retrieves a deep clone of a property by its index among the wrapped set's properties.
     *
     * @param propertyIndex the index of the property to retrieve
     * @return a deep clone of the {@link EnvironmentProperty} at the specified index
     */
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

    /**
     * Retrieves a deep clone of a property by name.
     *
     * @param propertyName the name of the property to retrieve
     * @return a deep clone of the {@link EnvironmentProperty} with the specified name
     */
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
