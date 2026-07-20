package modelarium.entities.contexts;

import modelarium.Config;
import modelarium.clock.Clock;
import modelarium.clock.MutableClock;
import modelarium.entities.Entity;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.attributes.AttributeBase;
import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.entities.immutable.ImmutableAgent;
import modelarium.entities.immutable.ImmutableAgentSet;
import modelarium.entities.immutable.ImmutableEnvironment;
import modelarium.exceptions.AgentNotFoundException;
import modelarium.exceptions.CoordinatorErrorException;
import modelarium.exceptions.CoordinatorTimeoutException;
import modelarium.exceptions.SimulationInterruptedException;
import modelarium.internal.Internal;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.utils.Cloners;

import java.util.List;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;

/**
 * Provides a model element (either an agent or the environment) with access
 * to relevant simulation resources such as the local environment, other agents,
 * and shared utilities, including communication and caching systems.
 *
 * <p>This class abstracts access logic based on model settings, including:
 * <ul>
 *     <li>Local access versus coordinated inter-thread access</li>
 *     <li>Optional caching of agents and environments</li>
 *     <li>Safe agent filtering with predicate functions</li>
 *     <li>The associated model clock</li>
 * </ul>
 */
public sealed abstract class SimulationContext implements Context permits AgentSimulationContext, EnvironmentSimulationContext {
    /** The entity this context belongs to */
    private final Entity<?,?,?,?> entity;

    /** The agents living on the same core as the owning entity */
    private final AgentSet localAgentSet;

    /** Global simulation configuration */
    private final Config config;

    /** The cache used to avoid repeated co-ordinator requests within a tick */
    private final ContextCache cache;

    /** The clock the context provides access to */
    private final MutableClock clock;

    /** Controller that manages the request and response queues for inter-thread communication */
    private final RequestResponseController requestResponseController;

    /** The interface this context uses to make requests to the co-ordinator, bound to the owning entity's name */
    private final RequestResponseInterface requestResponseInterface;

    /** The environment local to the owning entity's core */
    private final Environment localEnvironment;

    /** The random generator the owning entity can use */
    private final RandomGenerator randomGenerator;

    /** The attribute set currently being run on the owning entity */
    private AttributeSet<?,?> attributeSet = null;

    /** The attribute currently being run on the owning entity */
    private AttributeBase<?> attribute = null;

    /**
     * Constructs a new simulation context for the given entity.
     *
     * @param entity the entity the context belongs to
     * @param localAgentSet the local agent set the context will provide access to
     * @param config the shared model config
     * @param cache the cache the context can use for agents and the environment
     * @param clock the clock the context will provide access to
     * @param requestResponseController the request/response controller the context will need for inter-entity
     *                                  interaction
     * @param localEnvironment the local environment the context will provide access to
     * @param randomGenerator the random generator the context will provide access to
     */
    @Internal
    public SimulationContext(
            Entity<?,?,?,?> entity,
            AgentSet localAgentSet,
            Config config,
            ContextCache cache,
            MutableClock clock,
            RequestResponseController requestResponseController,
            Environment localEnvironment,
            RandomGenerator randomGenerator
    ) {
        this.entity = entity;
        this.localAgentSet = localAgentSet;
        this.config = config;
        this.cache = cache;
        this.clock = clock;
        this.requestResponseController = requestResponseController;
        this.requestResponseInterface = requestResponseController.getInterface(entity().name());
        this.localEnvironment = localEnvironment;
        this.randomGenerator = randomGenerator;
    }

    /**
     * Returns the model's clock.
     *
     * @return a read-only view of the model's {@link Clock}
     */
    public Clock getClock() {
        return clock;
    }

    /**
     * Returns whether an agent with the given name exists on the current core.
     *
     * @param agentName the name of the agent to check for
     * @return true if the agent exists in this core's local agent set, false otherwise
     */
    public boolean doesAgentExistInThisCore(String agentName) {
        return localAgentSet.doesAgentExist(agentName);
    }

    /**
     * Returns the random generator the owning entity can use.
     *
     * @return the entity's {@link RandomGenerator} instance
     */
    public RandomGenerator getRandom() {
        return randomGenerator;
    }

    /**
     * Records the attribute set currently being run on the owning entity.
     *
     * @param attributeSet the attribute set now being run
     */
    @Internal
    public void setCurrentAttributeSet(AttributeSet<?,?> attributeSet) {
        this.attributeSet = attributeSet;
    }

    /**
     * Records the attribute currently being run on the owning entity.
     *
     * @param attribute the attribute now being run
     */
    @Internal
    public void setCurrentAttribute(AttributeBase<?> attribute) {
        this.attribute = attribute;
    }

    /**
     * Returns the entity this context belongs to.
     *
     * @return the owning entity
     */
    protected Entity<?,?,?,?> entity() {
        return entity;
    }

    /**
     * Returns the attribute set currently being run on the owning entity.
     *
     * @return the current attribute set
     */
    protected AttributeSet<?,?> attributeSet() {
        return attributeSet;
    }

    /**
     * Returns the attribute currently being run on the owning entity.
     *
     * @return the current attribute
     */
    protected AttributeBase<?> attribute() {
        return attribute;
    }

    /**
     * Returns the model's configuration settings.
     *
     * @return the global model settings
     */
    protected Config config() {
        return config;
    }

    /**
     * Returns the cache this context uses for agents and the environment.
     *
     * @return the context's {@link ContextCache} instance
     */
    protected ContextCache cache() {
        return cache;
    }

    /**
     * Returns the interface this context uses to make requests to the co-ordinator.
     *
     * @return the context's {@link RequestResponseInterface} instance
     */
    protected RequestResponseInterface requestResponseInterface() {
        return requestResponseInterface;
    }

    /**
     * Returns the entity this context belongs to. Must be implemented by subclasses.
     *
     * @return the owning entity
     */
    public abstract Entity<?,?,?,?> getThisEntity();

    /**
     * Returns the attribute set currently being run on the owning entity. Must be implemented by subclasses.
     *
     * @return the current attribute set
     */
    public abstract AttributeSet<?,?> getThisAttributeSet();

    /**
     * Returns the attribute currently being run on the owning entity. Must be implemented by subclasses.
     *
     * @return the current attribute
     */
    public abstract AttributeBase<?> getThisAttribute();

    /**
     * Returns the model's environment. Must be implemented by subclasses.
     *
     * @return a read-only view of the model's environment
     */
    public abstract ImmutableEnvironment getEnvironment();

    /**
     * Creates and sets a simulation context for a newly added agent, sharing this context's resources.
     *
     * @param agent the agent to create a context for
     */
    private void createAgentContext(Agent agent) {
        agent.createContext(
                localAgentSet,
                config,
                cache,
                clock,
                requestResponseController,
                localEnvironment,
                randomGenerator
        );
    }

    /**
     * Creates and sets a simulation context for each agent in an agent set.
     *
     * @param agentSet the agents to create contexts for
     */
    private void createAgentContexts(AgentSet agentSet) {
        for (Agent agent : agentSet)
            createAgentContext(agent);
    }

    /**
     * Creates and sets a simulation context for each agent in a list.
     *
     * @param agentList the agents to create contexts for
     */
    private void createAgentContexts(List<Agent> agentList) {
        for (Agent agent : agentList)
            createAgentContext(agent);
    }

    /**
     * Adds an agent to the current core's local agent set, creating the context the agent needs to run.
     *
     * @param agent the agent to add
     */
    public void addAgent(Agent agent) {
        createAgentContext(agent);
        localAgentSet.add(agent);
    }

    /**
     * Adds each agent in an agent set to the current core's local agent set, creating the contexts the agents need
     * to run.
     *
     * @param agentSet the agents to add
     */
    public void addAgents(AgentSet agentSet) {
        createAgentContexts(agentSet);
        localAgentSet.add(agentSet);
    }

    /**
     * Adds each agent in a list to the current core's local agent set, creating the contexts the agents need to run.
     *
     * @param agentList the agents to add
     */
    public void addAgents(List<Agent> agentList) {
        createAgentContexts(agentList);
        localAgentSet.add(agentList);
    }

    /**
     * Adds a deep copy of an agent to the current core's local agent set.
     *
     * @param agent the agent to deep clone and add
     */
    public void addAgentDeepCopy(Agent agent) {
        addAgent(Cloners.standard().deepClone(agent));
    }

    /**
     * Adds a deep copy of each agent in an agent set to the current core's local agent set.
     *
     * @param agentSet the agents to deep clone and add
     */
    public void addAgentsDeepCopy(AgentSet agentSet) {
        addAgents(Cloners.standard().deepClone(agentSet));
    }

    /**
     * Adds a deep copy of each agent in a list to the current core's local agent set.
     *
     * @param agentList the agents to deep clone and add
     */
    public void addAgentsDeepCopy(List<Agent> agentList) {
        addAgents(Cloners.standard().deepClone(agentList));
    }

    /**
     * Retrieves an agent by name, whether it lives on this core or (in a synchronised model) on another core.
     *
     * <p>The agent is looked up in the local agent set first, then in the cache, and finally requested from the
     * co-ordinator if the model's threads are synchronised. Agents retrieved from the co-ordinator are cached for
     * the remainder of the tick.
     *
     * @param targetAgentName the name of the agent to retrieve
     * @return a read-only view of the requested agent
     */
    public ImmutableAgent getAgent(String targetAgentName) {
        // Check local agent set
        if (doesAgentExistInThisCore(targetAgentName))
            return new ImmutableAgent(localAgentSet.get(targetAgentName));

        // Check cache if enabled
        if (cache.doesAgentExist(targetAgentName))
            return new ImmutableAgent(cache.getAgent(targetAgentName));

        // If not synchronised, cannot retrieve further
        if (!config.areThreadsSynced())
            throw new AgentNotFoundException("Agent '" + targetAgentName + "' requested by '" + entity.name()
                    + "' not found in this thread (threads are not synced)");

        // Request from coordinator
        try {
            Agent requestedAgent = requestResponseInterface.getAgentFromCoordinator(entity.name(), targetAgentName);
            cache.addAgent(requestedAgent);
            return new ImmutableAgent(requestedAgent);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SimulationInterruptedException("Interrupted while fetching agent '" + targetAgentName + "'", e);
        } catch (CoordinatorTimeoutException | CoordinatorErrorException e) {
            throw new AgentNotFoundException("Agent '" + targetAgentName + "' requested by '" + entity.name()
                    + "' not found", e);
        }
    }

    /**
     * Retrieves the agents matching a filter, drawn from the whole population in a synchronised model or from this
     * core's local agents otherwise.
     *
     * <p>Filter results are cached for the remainder of the tick, keyed by the filter instance itself.
     *
     * @param filter a predicate to apply to each agent
     * @return a read-only view of the matching agents
     */
    public ImmutableAgentSet getFilteredAgents(Predicate<Agent> filter) {
        // Return cached filtered result if available
        if (cache.doesAgentFilterExist(filter))
            return cache.getFilteredAgents(filter).getAsImmutable();

        AgentSet filteredAgentSet;

        if (config.areThreadsSynced()) {
            // Request filtered agents from the coordinator
            try {
                filteredAgentSet = requestResponseInterface.getFilteredAgentsFromCoordinator(entity.name(), filter);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SimulationInterruptedException("Interrupted while retrieving filtered agents requested by " +
                        "'" + entity.name() + "'", e);
            } catch (CoordinatorTimeoutException | CoordinatorErrorException e) {
                throw new AgentNotFoundException("Failed to retrieve filtered agents requested by '" + entity.name()
                        + "' from the coordinator", e);
            }
        } else {
            // Use only local agent set
            filteredAgentSet = localAgentSet.getFilteredAgents(filter);
        }

        // Cache the result for future access
        cache.addFilteredAgents(filter, filteredAgentSet);

        return filteredAgentSet.getAsImmutable();
    }
}
