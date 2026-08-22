package modelarium.entities.agents.immutable;

import modelarium.entities.ReadOnlyEntity;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.attributes.events.ReadOnlyEvent;
import modelarium.entities.attributes.properties.ReadOnlyProperty;
import modelarium.entities.attributes.routines.ReadOnlyRoutine;
import modelarium.entities.attributes.sets.immutable.ImmutableAgentAttributeSet;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.logging.AttributeSetLog;

/**
 * Class for providing a read-only view of an {@link Agent}.
 *
 * <p>This class wraps a mutable agent so that other model elements can inspect it without being able to modify it.
 */
public final class ReadOnlyAgent extends ReadOnlyEntity<AgentSimulationContext, AgentContext, MutableAgentAttributeSet, AttributeSetLog<AgentSimulationContext>> {
    /**
     * Constructs a new immutable agent wrapping the specified mutable agent.
     *
     * @param entity the mutable agent to provide a read-only view of
     */
    public ReadOnlyAgent(Agent entity) {
        super(entity);
    }

    /**
     * Retrieves a read-only view of one of the wrapped agent's attribute sets by index.
     *
     * @param attributeSetIndex the index of the attribute set to retrieve
     * @return a new {@link ImmutableAgentAttributeSet} instance wrapping the attribute set at the specified index
     */
    @Override
    public ImmutableAgentAttributeSet getAttributeSet(int attributeSetIndex) {
        return new ImmutableAgentAttributeSet(getMutableEntity().getAttributeSet(attributeSetIndex));
    }

    /**
     * Retrieves a read-only view of one of the wrapped agent's attribute sets by name.
     *
     * @param attributeSetName the name of the attribute set to retrieve
     * @return a new {@link ImmutableAgentAttributeSet} instance wrapping the attribute set with the specified name
     */
    @Override
    public ImmutableAgentAttributeSet getAttributeSet(String attributeSetName) {
        return new ImmutableAgentAttributeSet(getMutableEntity().getAttributeSet(attributeSetName));
    }

    /**
     * Retrieves a read-only view of an event attribute from one of the wrapped agent's attribute sets.
     *
     * @param attributeSetName the name of the attribute set containing the event
     * @param eventName the name of the event to retrieve
     * @return the {@link ReadOnlyEvent} with the specified name
     */
    public ReadOnlyEvent getEvent(String attributeSetName, String eventName) {
        return getAttributeSet(attributeSetName).getEvent(eventName).getAsImmutable();
    }

    /**
     * Retrieves a read-only view of a routine attribute from one of the wrapped agent's attribute sets.
     *
     * @param attributeSetName the name of the attribute set containing the routine
     * @param routineName the name of the routine to retrieve
     * @return the {@link ReadOnlyRoutine} with the specified name
     */
    public ReadOnlyRoutine getRoutine(String attributeSetName, String routineName) {
        return getAttributeSet(attributeSetName).getRoutine(routineName).getAsImmutable();
    }

    /**
     * Retrieves a read-only view of a property attribute from one of the wrapped agent's attribute sets.
     *
     * @param attributeSetName the name of the attribute set containing the property
     * @param propertyName the name of the property to retrieve
     * @return the {@link ReadOnlyProperty} with the specified name
     */
    public ReadOnlyProperty<?> getProperty(String attributeSetName, String propertyName) {
        return getAttributeSet(attributeSetName).getProperty(propertyName).getAsImmutable();
    }

    /**
     * Returns whether the agent is dead or not.
     *
     * @return the boolean value of whether the agent is dead (true) or not (false)
     */
    public boolean isDead() {
        return ((Agent) getMutableEntity()).isDead();
    }
}
