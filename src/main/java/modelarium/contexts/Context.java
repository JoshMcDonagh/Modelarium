package modelarium.contexts;

import modelarium.Clock;
import modelarium.Config;
import modelarium.Entity;
import modelarium.agents.Agent;
import modelarium.agents.sets.AgentSet;
import modelarium.agents.sets.ImmutableAgentSet;
import modelarium.environments.Environment;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

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
public abstract class Context {

    private final Entity entity;
    private final AgentSet localAgentSet;
    private final Config config;
    private final ContextCache cache;
    private final RequestResponseInterface requestResponseInterface;

    private Clock clock = null;

    public Context(
            Entity entity,
            AgentSet localAgentSet,
            Config config,
            ContextCache cache,
            Clock clock,
            RequestResponseInterface requestResponseInterface
    ) {
        this.entity = entity;
        this.localAgentSet = localAgentSet;
        this.config = config;
        this.cache = cache;
        this.requestResponseInterface = requestResponseInterface;
    }

    public Clock getClock() {
        return clock;
    }

    public boolean doesAgentExistInThisCore(String agentName) {
        return localAgentSet.doesAgentExist(agentName);
    }

    protected Entity entity() {
        return entity;
    }

    protected Config config() {
        return config;
    }

    protected ContextCache cache() {
        return cache;
    }

    protected RequestResponseInterface requestResponseInterface() {
        return requestResponseInterface;
    }

    public abstract Entity getThisEntity();

    public abstract Environment getEnvironment();

    public Agent getAgent(String targetAgentName) {
        // Check local agent set
        if (doesAgentExistInThisCore(targetAgentName))
            return localAgentSet.get(targetAgentName);

        // Check cache if enabled
        if (cache.doesAgentExist(targetAgentName))
            return cache.getAgent(targetAgentName);

        // If not synchronised, cannot retrieve further
        if (!config.areProcessesSynced())
            return null;

        // Request from coordinator
        try {
            Agent requestedAgent = requestResponseInterface.getAgentFromCoordinator(entity.name(), targetAgentName);
            cache.addAgent(requestedAgent);
            return requestedAgent;
        } catch (Exception e) {
            return null;
        }
    }

    public ImmutableAgentSet getFilteredAgents(Predicate<Agent> filter) {
        // Return cached filtered result if available
        if (cache.doesAgentFilterExist(filter))
            return cache.getFilteredAgents(filter).getAsImmutable();

        AgentSet filteredAgentSet;

        if (config.areProcessesSynced()) {
            // Request filtered agents from the coordinator
            try {
                filteredAgentSet = requestResponseInterface.getFilteredAgentsFromCoordinator(entity.name(), filter);
            } catch (Exception e) {
                return null;
            }
        } else {
            // Use only local agent set
            filteredAgentSet = localAgentSet.getFilteredAgents(filter);
        }

        // Cache the result for future access
        cache.addAgentFilter(filter);
        cache.addAgents(filteredAgentSet);

        return filteredAgentSet.getAsImmutable();
    }
}
