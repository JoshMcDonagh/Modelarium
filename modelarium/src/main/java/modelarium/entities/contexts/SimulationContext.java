package modelarium.entities.contexts;

import modelarium.Config;
import modelarium.clock.Clock;
import modelarium.entities.Entity;
import modelarium.entities.readonly.ReadOnlyAgent;
import modelarium.entities.agentsets.ReadOnlyAgentSet;
import modelarium.entities.Agent;
import modelarium.entities.agentsets.AgentSet;
import modelarium.entities.attributes.sets.AttributeBase;
import modelarium.entities.attributes.sets.AttributeSet;
import modelarium.entities.Environment;
import modelarium.entities.readonly.ReadOnlyEnvironment;
import modelarium.exceptions.*;
import modelarium.internal.Internal;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

import java.util.ArrayList;
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
    private final Clock clock;

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

    /** Agent set for tracking agents added by this context's entity */
    private final AgentSet addedAgents = new AgentSet();

    /** List of agent names killed by this context's entity */
    private final List<String> killedAgentNames = new ArrayList<>();

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
            Clock clock,
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
     * Returns the entity's local agent set.
     *
     * @return the local agent set
     */
    protected AgentSet localAgentSet() {
        return localAgentSet;
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
     * Returns the model's environment.
     *
     * <p>If the model's threads are not synchronised, the core's local environment is returned. Otherwise, the
     * environment is taken from the cache if present, or requested from the co-ordinator and cached for the
     * remainder of the tick.
     *
     * @return a read-only view of the model's {@link Environment}
     */
    public ReadOnlyEnvironment getEnvironment() {
        if (!config.areThreadsSynced())
            return new ReadOnlyEnvironment(localEnvironment);

        // Return cached environment if available
        if (cache.doesEnvironmentExist())
            return cache.getEnvironment();

        // Request environment from coordinator
        ReadOnlyEnvironment requestedEnvironment;
        try {
            requestedEnvironment = requestResponseInterface.getEnvironmentFromCoordinator(entity.name());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SimulationInterruptedException("Interrupted while fetching environment requested by '"
                    + entity.name() + "'", e);
        } catch (CoordinatorTimeoutException | CoordinatorErrorException e) {
            throw new EnvironmentNotFoundException("Environment requested by '" + entity.name()
                    + "' could not be found", e);
        }

        // Cache the result
        cache.addEnvironment(requestedEnvironment);

        return requestedEnvironment;
    }

    /**
     * Adds an agent to the current core's local agent set, creating the context the agent needs to run.
     *
     * @param agent the agent to add
     */
    public void addAgent(Agent agent) {
        addedAgents.add(agent);
    }

    /**
     * Adds each agent in an agent set to the current core's local agent set, creating the contexts the agents need
     * to run.
     *
     * @param agentSet the agents to add
     */
    public void addAgents(AgentSet agentSet) {
        addedAgents.add(agentSet);
    }

    /**
     * Adds each agent in a list to the current core's local agent set, creating the contexts the agents need to run.
     *
     * @param agentList the agents to add
     */
    public void addAgents(List<Agent> agentList) {
        addedAgents.add(agentList);
    }

    /**
     * Returns the model's current population size.
     *
     * <p>The current population size is looked up in the cache. If it hasn't been cached, the value is requested
     * from the co-ordinator and then cached. If the model's threads are unsynchronised, the local current population size is
     * returned instead.</p>
     *
     * @return the current population size as an int
     */
    public int getCurrentPopulationSize() {
        if (!config.areThreadsSynced())
            return localAgentSet.size();

        if (cache.doesCurrentPopulationSizeExist())
            return cache.getCurrentPopulationSize();

        try{
            int currentPopulationSize = requestResponseInterface.getCurrentPopulationSizeFromCoordinator(entity.name());
            cache.addCurrentPopulationSize(currentPopulationSize);
            return currentPopulationSize;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SimulationInterruptedException("Interrupted while fetching the current population size", e);
        }
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
    public ReadOnlyAgent getAgent(String targetAgentName) {
        // Check local agent set
        if (doesAgentExistInThisCore(targetAgentName))
            return new ReadOnlyAgent(localAgentSet.get(targetAgentName));

        // Check cache if enabled
        if (cache.doesAgentExist(targetAgentName))
            return cache.getAgent(targetAgentName);

        // If not synchronised, cannot retrieve further
        if (!config.areThreadsSynced())
            throw new AgentNotFoundException("Agent '" + targetAgentName + "' requested by '" + entity.name()
                    + "' not found in this thread (threads are not synced)");

        // Request from coordinator
        try {
            ReadOnlyAgent requestedAgent = requestResponseInterface.getAgentFromCoordinator(entity.name(), targetAgentName);
            if (requestedAgent.isDead())
                throw new AgentIsDeadException("Agent '" + targetAgentName + "' requested by '" + entity.name()
                        + "' is dead and not accessible");
            cache.addAgent(requestedAgent);
            return requestedAgent;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SimulationInterruptedException("Interrupted while fetching agent '" + targetAgentName + "'", e);
        } catch (CoordinatorTimeoutException | CoordinatorErrorException e) {
            throw new AgentNotFoundException("Agent '" + targetAgentName + "' requested by '" + entity.name()
                    + "' not found", e);
        }
    }

    /**
     * Retrieves the agents (excluding dead agents) matching a filter, drawn from the whole population in a synchronised model or from this
     * core's local agents otherwise.
     *
     * <p>Filter results are cached for the remainder of the tick, keyed by the filter instance itself.
     *
     * @param filter a predicate to apply to each agent
     * @return a read-only view of the matching agents
     */
    public ReadOnlyAgentSet getFilteredAgents(Predicate<ReadOnlyAgent> filter) {
        return getFilteredAgents(filter, false);
    }

    /**
     * Retrieves the agents matching a filter, drawn from the whole population in a synchronised model or from this
     * core's local agents otherwise.
     *
     * <p>Filter results are cached for the remainder of the tick, keyed by the filter instance itself.
     *
     * @param filter a predicate to apply to each agent
     * @param includeDeadAgents a boolean which determines if the filtered agents should include the dead agents or not
     * @return a read-only view of the matching agents
     */
    public ReadOnlyAgentSet getFilteredAgents(Predicate<ReadOnlyAgent> filter, boolean includeDeadAgents) {
        if (includeDeadAgents && cache.doesAgentFilterExist(filter))
            return cache.getFilteredAgents(filter);

        if (!includeDeadAgents && cache.doesLivingOnlyAgentFilterExist(filter))
            return cache.getLivingOnlyFilteredAgents(filter);

        ReadOnlyAgentSet filteredAgentSet;

        if (config.areThreadsSynced()) {
            if (cache.doesGlobalAgentSetExist()) {
                filteredAgentSet = cache.getGlobalAgentSet().getFilteredAgents(filter, includeDeadAgents);
            } else {
                try {
                    ReadOnlyAgentSet globalAgentSet = requestResponseInterface.getGlobalAgentSetFromCoordinator(
                            entity.name()
                    );
                    cache.addGlobalAgentSet(globalAgentSet);


                    filteredAgentSet = globalAgentSet.getFilteredAgents(filter, includeDeadAgents);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new SimulationInterruptedException("Interrupted while retrieving filtered agents requested by " +
                            "'" + entity.name() + "'", e);
                } catch (CoordinatorTimeoutException | CoordinatorErrorException e) {
                    throw new AgentNotFoundException("Failed to retrieve filtered agents requested by '" + entity.name()
                            + "' from the coordinator", e);
                }
            }

        } else {
            filteredAgentSet = localAgentSet.getFilteredAgents(filter, includeDeadAgents).getAsImmutable();
        }

        // Cache the result for future access
        if (includeDeadAgents)
            cache.addFilteredAgents(filter, filteredAgentSet);
        else
            cache.addLivingOnlyFilteredAgents(filter, filteredAgentSet);

        return filteredAgentSet;
    }

    /**
     * Kills the agent with the given name.
     *
     * @param agentName the agent's name as a string
     */
    public void killAgent(String agentName) {
        if (config.areThreadsSynced()) {
            // Validates that the agent exists in the model-wide state visible during this tick.
            getAgent(agentName);
        } else if (!doesAgentExistInThisCore(agentName)) {
            throw new AgentNotFoundException(
                    "Agent '" + agentName + "' requested by '"
                            + entity.name()
                            + "' not found in this thread "
                            + "(threads are not synced)"
            );
        }

        killedAgentNames.add(agentName);
    }

    /**
     * Kills the agent of the given {@link ReadOnlyAgent} instance.
     *
     * @param agent the immutable agent to kill
     */
    public void killAgent(ReadOnlyAgent agent) {
        killAgent(agent.name());
    }

    /**
     * Kills all the agents with names in a given {@link List<String>} instance.
     *
     * @param agentNames the list of names of agents to kill
     */
    public void killAgents(List<String> agentNames) {
        // Validate every name before queuing any of them.
        for (String agentName : agentNames) {
            if (config.areThreadsSynced()) {
                getAgent(agentName);
            } else if (!doesAgentExistInThisCore(agentName)) {
                throw new AgentNotFoundException(
                        "Agent '" + agentName + "' requested by '"
                                + entity.name()
                                + "' not found in this thread "
                                + "(threads are not synced)"
                );
            }
        }

        killedAgentNames.addAll(agentNames);
    }

    /**
     * Kills all the agents in a given {@link ReadOnlyAgentSet} instance.
     *
     * @param agentSet the immutable agent set of agents to kill
     */
    public void killAgents(ReadOnlyAgentSet agentSet) {
        ArrayList<String> agentNames = new ArrayList<>();
        for (ReadOnlyAgent agent : agentSet)
            agentNames.add(agent.name());
        killAgents(agentNames);
    }

    @Internal
    public AgentSet getAddedAgents() {
        return addedAgents;
    }

    @Internal
    public List<String> getKilledAgentNames() {
        return killedAgentNames;
    }

    @Internal
    public void clearPendingAgentChanges() {
        addedAgents.clear();
        killedAgentNames.clear();
    }
}
