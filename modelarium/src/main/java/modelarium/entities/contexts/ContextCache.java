package modelarium.entities.contexts;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.immutable.ImmutableAgent;
import modelarium.entities.agents.immutable.ImmutableAgentSet;
import modelarium.entities.agents.mutable.MutableAgentSet;
import modelarium.entities.environments.ImmutableEnvironment;
import modelarium.internal.Internal;

import java.util.*;
import java.util.function.Predicate;

/**
 * Class for caching the agents, filtered agent sets and environment a worker retrieves during a tick.
 *
 * <p>This class is responsible for avoiding repeated requests to the co-ordinator within a single tick. It is
 * cleared by the worker at the end of every tick so that stale state does not carry over.
 */
public class ContextCache {

    /** List of previously applied agent filters (for caching filtered sets) */
    private final IdentityHashMap<Predicate<Agent>, ImmutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();

    /** The individually retrieved agents cached during the current tick */
    private final List<ImmutableAgent> individualAgentCacheList = new ArrayList<>();;
    private final Map<String, Integer> individualAgentCacheMap = new HashMap<>();

    /** The environment cached during the current tick, or null if it has not been retrieved */
    private ImmutableEnvironment environment = null;

    /**
     * Constructs a new worker cache.
     */
    @Internal
    public ContextCache() { }

    /**
     * Clears the entire cache. Should be called at the end of each tick.
     */
    public void clear() {
        filteredAgentsCache.clear();
        individualAgentCacheList.clear();
        individualAgentCacheMap.clear();
        environment = null;
    }

    /**
     * Returns whether a filtered agent set has been cached for the given filter.
     *
     * @param agentFilter the filter to check for
     * @return true if a result is cached for the filter, false otherwise
     */
    public boolean doesAgentFilterExist(Predicate<Agent> agentFilter) {
        return filteredAgentsCache.containsKey(agentFilter);
    }

    /**
     * Caches the agents matching a filter.
     *
     * @param agentFilter the filter the agents were matched against
     * @param results the agents matching the filter
     */
    public void addFilteredAgents(Predicate<Agent> agentFilter, ImmutableAgentSet results) {
        filteredAgentsCache.put(agentFilter, results);
    }

    /**
     * Retrieves the cached agents matching a filter.
     *
     * @param agentFilter the filter the agents were matched against
     * @return the cached {@link MutableAgentSet} for the filter, or null if none is cached
     */
    public ImmutableAgentSet getFilteredAgents(Predicate<Agent> agentFilter) {
        return filteredAgentsCache.get(agentFilter);
    }

    /**
     * Returns whether an agent with the given name has been cached.
     *
     * @param agentName the name of the agent to check for
     * @return true if the agent is cached, false otherwise
     */
    public boolean doesAgentExist(String agentName) {
        return individualAgentCacheMap.containsKey(agentName);
    }

    /**
     * Retrieves a cached agent by name.
     *
     * @param agentName the name of the agent to retrieve
     * @return the cached agent with the specified name
     */
    public ImmutableAgent getAgent(String agentName) {
        return individualAgentCacheList.get(individualAgentCacheMap.get(agentName));
    }

    /**
     * Caches an individually retrieved agent.
     *
     * @param agent the agent to cache
     */
    public void addAgent(ImmutableAgent agent) {
        individualAgentCacheMap.put(agent.name(), individualAgentCacheList.size());
        individualAgentCacheList.add(agent);
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
     * @return the cached {@link ImmutableEnvironment} instance, or null if none is cached
     */
    public ImmutableEnvironment getEnvironment() {
        return environment;
    }

    /**
     * Caches the environment.
     *
     * @param environment the environment to cache
     */
    public void addEnvironment(ImmutableEnvironment environment) {
        this.environment = environment;
    }
}
