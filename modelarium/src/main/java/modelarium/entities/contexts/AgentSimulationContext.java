package modelarium.entities.contexts;

import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.agents.mutable.AgentSet;
import modelarium.entities.attributes.sets.mutable.AttributeBase;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.multithreading.requestresponse.RequestResponseController;

import java.util.random.RandomGenerator;

/**
 * Class for providing an agent and its attributes with access to relevant simulation resources.
 *
 * <p>This class is the concrete {@link AgentContext} implementation the model creates for each agent, combining the
 * shared behaviour of {@link SimulationContext} with agent-specific entity and environment access.
 */
public final class AgentSimulationContext extends SimulationContext implements AgentContext {

    /** The environment local to the owning agent's core */
    private final Environment localEnvironment;

    /**
     * Constructs a new simulation context for the given agent.
     *
     * @param entity the agent the context belongs to
     * @param localAgentSet the local agent set the context will provide access to
     * @param config the shared model config
     * @param cache the cache the context can use for agents and the environment
     * @param clock the clock the context will provide access to
     * @param requestResponseController the request/response controller the context will need for inter-entity
     *                                  interaction
     * @param localEnvironment the local environment the context will provide access to
     * @param randomGenerator the random generator the context will provide access to
     */
    public AgentSimulationContext(
            Agent entity,
            AgentSet localAgentSet,
            Config config,
            ContextCache cache,
            MutableClock clock,
            RequestResponseController requestResponseController,
            Environment localEnvironment,
            RandomGenerator randomGenerator
    ) {
        super(entity, localAgentSet, config, cache, clock, requestResponseController, localEnvironment, randomGenerator);
        this.localEnvironment = localEnvironment;
    }

    /**
     * Returns the agent this context belongs to.
     *
     * @return the owning {@link Agent} instance
     */
    @Override
    public Agent getThisEntity() {
        return (Agent) entity();
    }

    /**
     * Returns the attribute set currently being run on the owning agent.
     *
     * @return the current {@link MutableAgentAttributeSet} instance
     */
    @Override
    public MutableAgentAttributeSet getThisAttributeSet() {
        return (MutableAgentAttributeSet) attributeSet();
    }

    /**
     * Returns the attribute currently being run on the owning agent.
     *
     * @return the current attribute instance
     */
    @Override
    public AttributeBase<AgentSimulationContext> getThisAttribute() {
        // noinspection unchecked
        return (AttributeBase<AgentSimulationContext>) attribute();
    }
}
