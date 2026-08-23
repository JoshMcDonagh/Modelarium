package modelarium.entities.contexts;

import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.immutable.ReadOnlyAgent;
import modelarium.entities.agents.immutable.ReadOnlyAgentSet;
import modelarium.entities.agents.mutable.AgentSet;
import modelarium.entities.attributes.sets.mutable.AttributeBase;
import modelarium.entities.attributes.sets.mutable.MutableEnvironmentAttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.ReadOnlyEnvironment;
import modelarium.exceptions.AgentNotFoundException;
import modelarium.internal.Internal;
import modelarium.multithreading.requestresponse.RequestResponseController;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;

/**
 * Class for providing the environment and its attributes with access to relevant simulation resources.
 *
 * <p>This class is the concrete {@link EnvironmentContext} implementation the model creates for the environment,
 * combining the shared behaviour of {@link SimulationContext} with environment-specific entity access.
 */
public final class EnvironmentSimulationContext extends SimulationContext implements EnvironmentContext {
    /**
     * Constructs a new simulation context for the environment.
     *
     * @param entity the environment this context belongs to
     * @param localAgentSet the local agent set the context will provide access to
     * @param config the shared model config
     * @param cache the cache the context can use for agents and the environment
     * @param clock the clock the context will provide access to
     * @param requestResponseController the request/response controller the context will need for inter-entity
     *                                  interaction
     * @param localEnvironment the local environment the context will provide access to
     * @param randomGenerator the random generator the context will provide access to
     */
    public EnvironmentSimulationContext(
            Environment entity,
            AgentSet localAgentSet,
            Config config,
            ContextCache cache,
            MutableClock clock,
            RequestResponseController requestResponseController,
            Environment localEnvironment,
            RandomGenerator randomGenerator
    ) {
        super(entity, localAgentSet, config, cache, clock, requestResponseController, localEnvironment, randomGenerator);
    }

    /**
     * Internal method which returns the environment's local agent set.
     *
     * @return the environment's local agent set
     */
    @Internal
    public AgentSet getLocalAgentSet() {
        return localAgentSet();
    }

    /**
     * Returns the environment this context belongs to.
     *
     * @return the owning {@link Environment} instance
     */
    @Override
    public Environment getThisEntity() {
        return (Environment) entity();
    }

    /**
     * Returns the attribute set currently being run on the owning environment.
     *
     * @return the current {@link MutableEnvironmentAttributeSet} instance
     */
    @Override
    public MutableEnvironmentAttributeSet getThisAttributeSet() {
        return (MutableEnvironmentAttributeSet) attributeSet();
    }

    /**
     * Returns the attribute currently being run on the owning environment.
     *
     * @return the current attribute instance
     */
    @Override
    public AttributeBase<EnvironmentSimulationContext> getThisAttribute() {
        return (AttributeBase<EnvironmentSimulationContext>) attribute();
    }

    /**
     * Unsupported for the environment's own context.
     *
     * <p>The context requester is already the environment, so {@link #getThisEntity()} should be used instead.
     *
     * @return this method never returns normally
     */
    @Override
    public ReadOnlyEnvironment getEnvironment() {
        throw new UnsupportedOperationException("Context requester is already an Environment - use 'getThisEntity()' instead");
    }

    /**
     * Retrieves an agent by name.
     *
     * @param targetAgentName the name of the agent to retrieve
     * @return a read-only view of the requested agent
     */
    @Override
    public ReadOnlyAgent getAgent(String targetAgentName) {
        // Check local agent set
        if (doesAgentExistInThisCore(targetAgentName))
            return new ReadOnlyAgent(localAgentSet().get(targetAgentName));

        throw new AgentNotFoundException("Agent '" + targetAgentName + "' requested by '" + entity().name()
                + "' not found");
    }

    /**
     * Retrieves the agents matching a filter.
     *
     * @param filter a predicate to apply to each agent
     * @return a read-only view of the matching agents
     */
    @Override
    public ReadOnlyAgentSet getFilteredAgents(Predicate<ReadOnlyAgent> filter) {
        return localAgentSet().getFilteredAgents(filter).getAsImmutable();
    }

    /**
     * Kills the agent with the given name.
     *
     * @param agentName the agent's name as a string
     */
    @Override
    public void killAgent(String agentName) {
        if (!doesAgentExistInThisCore(agentName))
            throw new AgentNotFoundException("Agent '" + agentName + "' requested by '" + entity().name() + "' not found");
        localAgentSet().get(agentName).kill();
    }

    /**
     * Kills the agent of the given {@link ReadOnlyAgent} instance.
     *
     * @param agent the immutable agent to kill
     */
    @Override
    public void killAgent(ReadOnlyAgent agent) {
        killAgent(agent.name());
    }

    /**
     * Kills all the agents with names in a given {@link List<String>} instance.
     *
     * @param agentNames the list of names of agents to kill
     */
    @Override
    public void killAgents(List<String> agentNames) {
        for (String agentName : agentNames) {
            if (!doesAgentExistInThisCore(agentName))
                throw new AgentNotFoundException("Agent '" + agentName + "' requested by '" + entity().name()
                        + "' not found in this thread");
        }

        for (String agentName : agentNames)
            localAgentSet().get(agentName).kill();
    }

    /**
     * Kills all the agents in a given {@link ReadOnlyAgentSet} instance.
     *
     * @param agentSet the immutable agent set of agents to kill
     */
    @Override
    public void killAgents(ReadOnlyAgentSet agentSet) {
        ArrayList<String> agentNames = new ArrayList<>();
        for (ReadOnlyAgent agent : agentSet)
            agentNames.add(agent.name());
        killAgents(agentNames);
    }
}
