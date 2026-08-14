package modelarium.entities.contexts;

import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.attributes.sets.mutable.AttributeBase;
import modelarium.entities.environments.ImmutableEnvironment;
import modelarium.entities.environments.MutableEnvironment;

/**
 * Interface for providing an agent's attributes with access to their owning agent and the wider model.
 *
 * <p>This interface is the view of the simulation an agent's attributes are given, exposing the agent itself, the
 * attribute set and attribute currently being run, and the model's environment.
 */
public sealed interface AgentContext extends EntityContext permits AgentSimulationContext {

    /**
     * Returns the agent this context belongs to.
     *
     * @return the owning {@link MutableAgent} instance
     */
    MutableAgent getThisEntity();

    /**
     * Returns the attribute set currently being run on the owning agent.
     *
     * @return the current {@link MutableAgentAttributeSet} instance
     */
    MutableAgentAttributeSet getThisAttributeSet();

    /**
     * Returns the attribute currently being run on the owning agent.
     *
     * @return the current attribute instance
     */
    AttributeBase<AgentSimulationContext> getThisAttribute();

    /**
     * Returns the model's environment.
     *
     * @return a read-only view of the model's {@link MutableEnvironment}
     */
    ImmutableEnvironment getEnvironment();
}
