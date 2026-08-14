package modelarium.entities.attributes.sets;

import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.events.Event;
import modelarium.entities.attributes.sets.immutable.ImmutableAttributeSet;
import modelarium.entities.attributes.sets.mutable.MutableAttributeSet;
import modelarium.entities.attributes.properties.Property;
import modelarium.entities.attributes.routines.Routine;
import modelarium.entities.logging.AttributeSetLog;

/**
 * Interface for accessing a named group of an entity's attributes.
 */
public sealed interface AttributeSet permits AgentAttributeSet, EnvironmentAttributeSet, MutableAttributeSet, ImmutableAttributeSet {
    /**
     * Returns the name of this attribute set.
     *
     * @return the attribute set's name
     */
    String name();

    /**
     * Returns the number of attributes in this set.
     *
     * @return the attribute set's size
     */
    int size();

    /**
     * Returns the log recording the values of this set's logged attributes.
     *
     * @return the attribute set's {@link AttributeSetLog} instance
     */
    AttributeSetLog<?> getLog();

    /**
     * Retrieves a publicly accessible attribute by index.
     *
     * @param index the index of the attribute to retrieve
     * @return the attribute at the specified index
     */
    Attribute get(int index);

    /**
     * Retrieves a publicly accessible attribute by name.
     *
     * @param attributeName the name of the attribute to retrieve
     * @return the attribute with the specified name
     */
    Attribute get(String attributeName);

    /**
     * Retrieves an event by its index among this set's events.
     *
     * @param eventIndex the index of the event to retrieve
     * @return the event at the specified index
     */
    Event<?> getEvent(int eventIndex);

    /**
     * Retrieves an event by name.
     *
     * @param eventName the name of the event to retrieve
     * @return the event with the specified name
     */
    Event<?> getEvent(String eventName);

    /**
     * Retrieves a routine by its index among this set's routines.
     *
     * @param routineIndex the index of the routine to retrieve
     * @return the routine at the specified index
     */
    Routine<?> getRoutine(int routineIndex);

    /**
     * Retrieves a routine by name.
     *
     * @param routineName the name of the routine to retrieve
     * @return the routine with the specified name
     */
    Routine<?> getRoutine(String routineName);

    /**
     * Retrieves a property by its index among this set's properties.
     *
     * @param propertyIndex the index of the property to retrieve
     * @return the property at the specified index
     */
    Property<?,?> getProperty(int propertyIndex);

    /**
     * Retrieves a property by name.
     *
     * @param propertyName the name of the property to retrieve
     * @return the property with the specified name
     */
    Property<?,?> getProperty(String propertyName);
}
