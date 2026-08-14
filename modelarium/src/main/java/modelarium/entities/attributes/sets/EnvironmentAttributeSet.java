package modelarium.entities.attributes.sets;

import modelarium.entities.attributes.EnvironmentAttribute;
import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.sets.immutable.ImmutableEnvironmentAttributeSet;
import modelarium.entities.attributes.sets.mutable.MutableEnvironmentAttributeSet;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.EnvironmentRoutine;

/**
 * Interface for accessing a named group of an environment's attributes.
 */
public sealed interface EnvironmentAttributeSet extends AttributeSet permits MutableEnvironmentAttributeSet, ImmutableEnvironmentAttributeSet {
    /**
     * Retrieves a publicly accessible attribute by index.
     *
     * @param index the index of the attribute to retrieve
     * @return the {@link EnvironmentAttribute} at the specified index
     */
    EnvironmentAttribute get(int index);

    /**
     * Retrieves a publicly accessible attribute by name.
     *
     * @param attributeName the name of the attribute to retrieve
     * @return the {@link EnvironmentAttribute} with the specified name
     */
    EnvironmentAttribute get(String attributeName);

    /**
     * Retrieves an event by its index among this set's events.
     *
     * @param eventIndex the index of the event to retrieve
     * @return the {@link EnvironmentEvent} at the specified index
     */
    EnvironmentEvent getEvent(int eventIndex);

    /**
     * Retrieves an event by name.
     *
     * @param eventName the name of the event to retrieve
     * @return the {@link EnvironmentEvent} with the specified name
     */
    EnvironmentEvent getEvent(String eventName);

    /**
     * Retrieves a routine by its index among this set's routines.
     *
     * @param routineIndex the index of the routine to retrieve
     * @return the {@link EnvironmentRoutine} at the specified index
     */
    EnvironmentRoutine getRoutine(int routineIndex);

    /**
     * Retrieves a routine by name.
     *
     * @param routineName the name of the routine to retrieve
     * @return the {@link EnvironmentRoutine} with the specified name
     */
    EnvironmentRoutine getRoutine(String routineName);

    /**
     * Retrieves a property by its index among this set's properties.
     *
     * @param propertyIndex the index of the property to retrieve
     * @return the {@link EnvironmentProperty} at the specified index
     */
    EnvironmentProperty<?> getProperty(int propertyIndex);

    /**
     * Retrieves a property by name.
     *
     * @param propertyName the name of the property to retrieve
     * @return the {@link EnvironmentProperty} with the specified name
     */
    EnvironmentProperty<?> getProperty(String propertyName);
}
