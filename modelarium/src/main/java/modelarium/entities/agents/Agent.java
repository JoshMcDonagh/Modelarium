package modelarium.entities.agents;

import modelarium.entities.Entity;
import modelarium.entities.agents.immutable.ImmutableAgent;
import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.attributes.sets.AgentAttributeSet;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.EntityLog;

/**
 * Interface for representing an agent in the model.
 */
public sealed interface Agent extends Entity permits MutableAgent, ImmutableAgent {
    /**
     * Returns the name of this agent.
     *
     * @return the agent's name
     */
    String name();

    /**
     * Returns the number of attribute sets this agent owns.
     *
     * @return the entity's attribute set count
     */
    int attributeSetCount();

    /**
     * Returns the total number of attributes across all of this agent's attribute sets.
     *
     * @return the agent's total attribute count
     */
    int attributeCount();

    /**
     * Retrieves an attribute set by index.
     *
     * @param attributeSetIndex the index of the attribute set to retrieve
     * @return the attribute set at the specified index
     */
    AgentAttributeSet getAttributeSet(int attributeSetIndex);

    /**
     * Retrieves an attribute set by name.
     *
     * @param attributeSetName the name of the attribute set to retrieve
     * @return the attribute set with the specified name
     */
    AgentAttributeSet getAttributeSet(String attributeSetName);

    /**
     * Returns the log of this agent's attribute values.
     *
     * @return a new {@link EntityLog} instance containing the logs of the agent's attribute sets
     */
    EntityLog<
            AgentSimulationContext,
            AgentContext,
            MutableAgentAttributeSet,
            AttributeSetLog<AgentSimulationContext>
            > getLog();
}
