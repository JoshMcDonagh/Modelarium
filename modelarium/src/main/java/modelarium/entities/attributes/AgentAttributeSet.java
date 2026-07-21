package modelarium.entities.attributes;

import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;

import java.util.List;

/**
 * Class for containing and managing a named group of an agent's attributes.
 *
 * <p>This class specialises {@link AttributeSet} so that retrieved attributes are returned as their
 * agent-flavoured types.
 */
public final class AgentAttributeSet extends AttributeSet<AgentSimulationContext, AgentContext> {

    /**
     * Constructs a new agent attribute set with the specified owner, name and attributes.
     *
     * @param name the name of the attribute set, used to identify it within its owning agent
     * @param attributes the attributes the set will contain, in the order they will be run
     */
    public AgentAttributeSet(String name, List<Attribute> attributes) {
        super(name, attributes);
    }

    /**
     * Retrieves a publicly accessible attribute by index.
     *
     * @param index the index of the attribute to retrieve
     * @return the {@link AgentAttribute} at the specified index
     */
    public AgentAttribute get(int index) {
        return (AgentAttribute) super.get(index);
    }

    /**
     * Retrieves a publicly accessible attribute by name.
     *
     * @param attributeName the name of the attribute to retrieve
     * @return the {@link AgentAttribute} with the specified name
     */
    public AgentAttribute get(String attributeName) {
        return (AgentAttribute) super.get(attributeName);
    }

    /**
     * Retrieves an event by its index among this set's events.
     *
     * @param eventIndex the index of the event to retrieve
     * @return the {@link AgentEvent} at the specified index
     */
    public AgentEvent getEvent(int eventIndex) {
        return (AgentEvent) super.getEvent(eventIndex);
    }

    /**
     * Retrieves an event by name.
     *
     * @param eventName the name of the event to retrieve
     * @return the {@link AgentEvent} with the specified name
     */
    public AgentEvent getEvent(String eventName) {
        return (AgentEvent) super.getEvent(eventName);
    }

    /**
     * Retrieves a routine by its index among this set's routines.
     *
     * @param routineIndex the index of the routine to retrieve
     * @return the {@link AgentRoutine} at the specified index
     */
    public AgentRoutine getRoutine(int routineIndex) {
        return (AgentRoutine) super.getRoutine(routineIndex);
    }

    /**
     * Retrieves a routine by name.
     *
     * @param routineName the name of the routine to retrieve
     * @return the {@link AgentRoutine} with the specified name
     */
    public AgentRoutine getRoutine(String routineName) {
        return (AgentRoutine) super.getRoutine(routineName);
    }

    /**
     * Retrieves a property by its index among this set's properties.
     *
     * @param propertyIndex the index of the property to retrieve
     * @return the {@link AgentProperty} at the specified index
     */
    public AgentProperty<?> getProperty(int propertyIndex) {
        return (AgentProperty<?>) super.getProperty(propertyIndex);
    }

    /**
     * Retrieves a property by name.
     *
     * @param propertyName the name of the property to retrieve
     * @return the {@link AgentProperty} with the specified name
     */
    public AgentProperty<?> getProperty(String propertyName) {
        return (AgentProperty<?>) super.getProperty(propertyName);
    }
}
