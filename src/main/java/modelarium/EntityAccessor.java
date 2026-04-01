package modelarium;

import modelarium.agents.Agent;
import modelarium.agents.AgentSet;
import modelarium.environments.Environment;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.multithreading.utils.WorkerCache;

import java.util.function.Predicate;

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
public class EntityAccessor {

    private final Entity entity;
    private final AgentSet localAgentSet;
    private final ModelConfig settings;
    private final WorkerCache cache;
    private final RequestResponseInterface requestResponseInterface;
    private final Environment localEnvironment;

    private ModelClock clock = null;

    /**
     * Constructs a new accessor for a model element.
     *
     * @param entity              the model element (agent or environment) this accessor serves
     * @param localAgentSet             the set of agents assigned to this core/thread
     * @param settings                  the shared model settings object
     * @param cache                     the local cache for agent/environment access (may be null if not used)
     * @param requestResponseInterface  the communication interface for synchronised coordination
     * @param localEnvironment          the local copy of the environment assigned to this model element
     */
    public EntityAccessor(
            Entity entity,
            AgentSet localAgentSet,
            ModelConfig settings,
            WorkerCache cache,
            RequestResponseInterface requestResponseInterface,
            Environment localEnvironment
    ) {
        this.entity = entity;
        this.localAgentSet = localAgentSet;
        this.settings = settings;
        this.cache = cache;
        this.requestResponseInterface = requestResponseInterface;
        this.localEnvironment = localEnvironment;
    }

    /**
     * Sets the associated clock instance.
     * Once a clock has been set, it cannot be reset.
     *
     * @param clock the {@link ModelClock} instance to set with
     */
    public void setModelClock(ModelClock clock) {
        if (this.clock == null)
            this.clock = clock;
    }

    /**
     * Retrieves the associated clock instance.
     * @return the {@link ModelClock} instance
     */
    public ModelClock getModelClock() {
        return clock;
    }

    /**
     * Checks whether an agent with the given name exists in this core/thread.
     *
     * @param agentName the name of the agent to check
     * @return true if the agent exists locally
     */
    public boolean doesAgentExistInThisCore(String agentName) {
        return localAgentSet.doesAgentExist(agentName);
    }

    /**
     * Retrieves an agent by name, checking local storage first, then cache, then (if enabled) contacting the coordinator.
     *
     * @param targetAgentName the name of the agent to retrieve
     * @return the agent instance, or null if not found or if retrieval failed
     */
    public Agent getAgentByName(String targetAgentName) {
        // Check local agent set
        if (doesAgentExistInThisCore(targetAgentName))
            return localAgentSet.get(targetAgentName);

        // Check cache if enabled
        if (settings.getIsCacheUsed() && cache.doesAgentExist(targetAgentName))
            return cache.getAgent(targetAgentName);

        // If not synchronised, cannot retrieve further
        if (!settings.getAreProcessesSynced())
            return null;

        // Request from coordinator
        try {
            Agent requestedAgent = requestResponseInterface.getAgentFromCoordinator(entity.name(), targetAgentName);
            if (settings.getIsCacheUsed())
                cache.addAgent(requestedAgent);
            return requestedAgent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a set of agents that match the given filter predicate.
     * May involve coordinator requests or use of cached results, depending on settings.
     *
     * @param filter the predicate used to select agents
     * @return an {@link AgentSet} containing the matching agents, or null if retrieval fails
     */
    public AgentSet getFilteredAgents(Predicate<Agent> filter) {
        // Return cached filtered result if available
        if (settings.getIsCacheUsed() && cache.doesAgentFilterExist(filter))
            return cache.getFilteredAgents(filter);

        AgentSet filteredAgentSet;

        if (settings.getAreProcessesSynced()) {
            // Request filtered agents from the coordinator
            try {
                filteredAgentSet = requestResponseInterface.getFilteredAgentsFromCoordinator(entity.name(), filter);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            // Use only local agent set
            filteredAgentSet = localAgentSet.getFilteredAgents(filter);
        }

        // Cache the result for future access if enabled
        if (settings.getIsCacheUsed()) {
            cache.addAgentFilter(filter);
            cache.addAgents(filteredAgentSet);
        }

        return filteredAgentSet;
    }

    /**
     * Retrieves the current environment for the model element.
     * This may be the local environment, a cached copy, or one retrieved from the coordinator.
     *
     * @return the environment instance available to this model element
     */
    public Environment getEnvironment() {
        if (!settings.getAreProcessesSynced())
            return localEnvironment;

        // Return cached environment if available
        if (settings.getIsCacheUsed() && cache.doesEnvironmentExist())
            return cache.getEnvironment();

        // Request environment from coordinator
        Environment requestedEnvironment;
        try {
            requestedEnvironment = requestResponseInterface.getEnvironmentFromCoordinator(entity.name());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // Cache the result
        if (settings.getIsCacheUsed())
            cache.addEnvironment(requestedEnvironment);

        return requestedEnvironment;
    }
}
