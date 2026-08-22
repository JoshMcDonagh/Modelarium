package modelarium.entities.attributes.sets;

import modelarium.entities.attributes.AgentAttribute;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.attributes.sets.immutable.ImmutableAgentAttributeSet;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;

/**
 * Interface for accessing a named group of an agent's attributes.
 */
public sealed interface AgentAttributeSet extends AttributeSet permits MutableAgentAttributeSet, ImmutableAgentAttributeSet {
    /**
     * Retrieves a publicly accessible attribute by index.
     *
     * @param index the index of the attribute to retrieve
     * @return the {@link AgentAttribute} at the specified index
     */
    AgentAttribute get(int index);

    /**
     * Retrieves a publicly accessible attribute by name.
     *
     * @param attributeName the name of the attribute to retrieve
     * @return the {@link AgentAttribute} with the specified name
     */
    AgentAttribute get(String attributeName);

    /**
     * Retrieves an event by its index among this set's events.
     *
     * @param eventIndex the index of the event to retrieve
     * @return the {@link AgentEvent} at the specified index
     */
    AgentEvent getEvent(int eventIndex);

    /**
     * Retrieves an event by name.
     *
     * @param eventName the name of the event to retrieve
     * @return the {@link AgentEvent} with the specified name
     */
    AgentEvent getEvent(String eventName);

    /**
     * Retrieves a routine by its index among this set's routines.
     *
     * @param routineIndex the index of the routine to retrieve
     * @return the {@link AgentRoutine} at the specified index
     */
    AgentRoutine getRoutine(int routineIndex);

    /**
     * Retrieves a routine by name.
     *
     * @param routineName the name of the routine to retrieve
     * @return the {@link AgentRoutine} with the specified name
     */
    AgentRoutine getRoutine(String routineName);

    /**
     * Retrieves a property by its index among this set's properties.
     *
     * @param propertyIndex the index of the property to retrieve
     * @return the {@link AgentProperty} at the specified index
     */
    AgentProperty<?> getProperty(int propertyIndex);

    /**
     * Retrieves a property by name.
     *
     * @param propertyName the name of the property to retrieve
     * @return the {@link AgentProperty} with the specified name
     */
    AgentProperty<?> getProperty(String propertyName);
}
