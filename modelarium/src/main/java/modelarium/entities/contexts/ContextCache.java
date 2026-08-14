package modelarium.entities.contexts;

import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.agents.mutable.MutableAgentSet;
import modelarium.entities.environments.MutableEnvironment;
import modelarium.internal.Internal;

import java.util.IdentityHashMap;
import java.util.function.Predicate;

/**
 * Class for caching the agents, filtered agent sets and environment a worker retrieves during a tick.
 *
 * <p>This class is responsible for avoiding repeated requests to the co-ordinator within a single tick. It is
 * cleared by the worker at the end of every tick so that stale state does not carry over.
 */
public class ContextCache {

    /** List of previously applied agent filters (for caching filtered sets) */
    private final IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();

    /** The individually retrieved agents cached during the current tick */
    private final MutableAgentSet individualAgentCache;

    /** The environment cached during the current tick, or null if it has not been retrieved */
    private MutableEnvironment environment = null;

    /**
     * Constructs a new worker cache.
     */
    @Internal
    public ContextCache() {
        individualAgentCache = new MutableAgentSet();
    }

    /**
     * Clears the entire cache. Should be called at the end of each tick.
     */
    public void clear() {
        filteredAgentsCache.clear();
        individualAgentCache.clear();
        environment = null;
    }

    /**
     * Returns whether a filtered agent set has been cached for the given filter.
     *
     * @param agentFilter the filter to check for
     * @return true if a result is cached for the filter, false otherwise
     */
    public boolean doesAgentFilterExist(Predicate<MutableAgent> agentFilter) {
        return filteredAgentsCache.containsKey(agentFilter);
    }

    /**
     * Caches the agents matching a filter.
     *
     * @param agentFilter the filter the agents were matched against
     * @param results the agents matching the filter
     */
    public void addFilteredAgents(Predicate<MutableAgent> agentFilter, MutableAgentSet results) {
        filteredAgentsCache.put(agentFilter, results);
    }

    /**
     * Retrieves the cached agents matching a filter.
     *
     * @param agentFilter the filter the agents were matched against
     * @return the cached {@link MutableAgentSet} for the filter, or null if none is cached
     */
    public MutableAgentSet getFilteredAgents(Predicate<MutableAgent> agentFilter) {
        return filteredAgentsCache.get(agentFilter);
    }

    /**
     * Returns whether an agent with the given name has been cached.
     *
     * @param agentName the name of the agent to check for
     * @return true if the agent is cached, false otherwise
     */
    public boolean doesAgentExist(String agentName) {
        return individualAgentCache.doesAgentExist(agentName);
    }

    /**
     * Retrieves a cached agent by name.
     *
     * @param agentName the name of the agent to retrieve
     * @return the cached agent with the specified name
     */
    public MutableAgent getAgent(String agentName) {
        return individualAgentCache.get(agentName);
    }

    /**
     * Caches an individually retrieved agent.
     *
     * @param agent the agent to cache
     */
    public void addAgent(MutableAgent agent) {
        individualAgentCache.add(agent);
    }

    /**
     * Caches each agent in an agent set.
     *
     * @param agentSet the agents to cache
     */
    public void addAgents(MutableAgentSet agentSet) {
        individualAgentCache.add(agentSet);
    }

    /**
     * Returns whether the environment has been cached.
     *
     * @return true if the environment is cached, false otherwise
     */
    public boolean doesEnvironmentExist() {
        return environment != null;
    }

    /**
     * Retrieves the cached environment.
     *
     * @return the cached {@link MutableEnvironment} instance, or null if none is cached
     */
    public MutableEnvironment getEnvironment() {
        return environment;
    }

    /**
     * Caches the environment.
     *
     * @param environment the environment to cache
     */
    public void addEnvironment(MutableEnvironment environment) {
        this.environment = environment;
    }
}
