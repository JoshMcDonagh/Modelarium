package modelarium.entities.attributes.sets.readonly;

import modelarium.entities.attributes.AgentAttribute;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.attributes.sets.AgentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;

/**
 * Class for providing a read-only view of an {@link AgentAttributeSet}.
 *
 * <p>This class specialises {@link ReadOnlyAttributeSet} so that retrieved attributes are returned as deep clones
 * of their agent-flavoured types.
 */
public final class ReadOnlyAgentAttributeSet extends ReadOnlyAttributeSet<AgentSimulationContext, AgentContext> {

    /**
     * Constructs a new immutable agent attribute set wrapping the specified mutable attribute set.
     *
     * @param attributeSet the mutable agent attribute set to provide a read-only view of
     */
    public ReadOnlyAgentAttributeSet(AgentAttributeSet attributeSet) {
        super(attributeSet);
    }

    /**
     * Retrieves a deep clone of a publicly accessible attribute by index.
     *
     * @param index the index of the attribute to retrieve
     * @return a deep clone of the {@link AgentAttribute} at the specified index
     */
    @Override
    public AgentAttribute get(int index) {
        return getClonedAttribute(
                AgentAttributeSet.class,
                AgentAttribute.class,
                "get",
                int.class,
                index
        );
    }

    /**
     * Retrieves a deep clone of a publicly accessible attribute by name.
     *
     * @param attributeName the name of the attribute to retrieve
     * @return a deep clone of the {@link AgentAttribute} with the specified name
     */
    @Override
    public AgentAttribute get(String attributeName) {
        return getClonedAttribute(
                AgentAttributeSet.class,
                AgentAttribute.class,
                "get",
                String.class,
                attributeName
        );
    }

    /**
     * Retrieves a deep clone of an event by its index among the wrapped set's events.
     *
     * @param eventIndex the index of the event to retrieve
     * @return a deep clone of the {@link AgentEvent} at the specified index
     */
    @Override
    public AgentEvent getEvent(int eventIndex) {
        return getClonedAttribute(
                AgentAttributeSet.class,
                AgentEvent.class,
                "getEvent",
                int.class,
                eventIndex
        );
    }

    /**
     * Retrieves a deep clone of an event by name.
     *
     * @param eventName the name of the event to retrieve
     * @return a deep clone of the {@link AgentEvent} with the specified name
     */
    @Override
    public AgentEvent getEvent(String eventName) {
        return getClonedAttribute(
                AgentAttributeSet.class,
                AgentEvent.class,
                "getEvent",
                String.class,
                eventName
        );
    }

    /**
     * Retrieves a deep clone of a routine by its index among the wrapped set's routines.
     *
     * @param routineIndex the index of the routine to retrieve
     * @return a deep clone of the {@link AgentRoutine} at the specified index
     */
    @Override
    public AgentRoutine getRoutine(int routineIndex) {
        return getClonedAttribute(
                AgentAttributeSet.class,
                AgentRoutine.class,
                "getRoutine",
                int.class,
                routineIndex
        );
    }

    /**
     * Retrieves a deep clone of a routine by name.
     *
     * @param routineName the name of the routine to retrieve
     * @return a deep clone of the {@link AgentRoutine} with the specified name
     */
    @Override
    public AgentRoutine getRoutine(String routineName) {
        return getClonedAttribute(
                AgentAttributeSet.class,
                AgentRoutine.class,
                "getRoutine",
                String.class,
                routineName
        );
    }

    /**
     * Retrieves a deep clone of a property by its index among the wrapped set's properties.
     *
     * @param propertyIndex the index of the property to retrieve
     * @return a deep clone of the {@link AgentProperty} at the specified index
     */
    @Override
    public AgentProperty<?> getProperty(int propertyIndex) {
        return getClonedAttribute(
                AgentAttributeSet.class,
                AgentProperty.class,
                "getProperty",
                int.class,
                propertyIndex
        );
    }

    /**
     * Retrieves a deep clone of a property by name.
     *
     * @param propertyName the name of the property to retrieve
     * @return a deep clone of the {@link AgentProperty} with the specified name
     */
    @Override
    public AgentProperty<?> getProperty(String propertyName) {
        return getClonedAttribute(
                AgentAttributeSet.class,
                AgentProperty.class,
                "getProperty",
                String.class,
                propertyName
        );
    }
}
