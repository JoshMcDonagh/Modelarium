package modelarium.entities.contexts;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.MutableAgentSet;
import modelarium.entities.environments.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ContextCache {

    /** List of previously applied agent filters (for caching filtered sets) */
    private final List<Predicate<Agent>> agentFiltersCacheList = new ArrayList<>();

    /** Locally cached set of agents (filtered or directly fetched) */
    private final MutableAgentSet agentSetCache;

    /** Cached environment instance, if retrieved */
    private Environment environment = null;

    /**
     * Constructs a new worker cache.
     */
    public ContextCache() {
        agentSetCache = new MutableAgentSet(true);
    }

    /**
     * Clears the entire cache. Should be called at the end of each tick.
     */
    public void clear() {
        agentFiltersCacheList.clear();
        agentSetCache.clear();
        environment = null;
    }

    /**
     * Checks whether a specific agent filter has already been applied and cached.
     *
     * @param agentFilter the predicate used to filter agents
     * @return true if this filter has already been used
     */
    public boolean doesAgentFilterExist(Predicate<Agent> agentFilter) {
        return agentFiltersCacheList.contains(agentFilter);
    }

    /**
     * Adds a new agent filter to the list of cached filters.
     *
     * @param agentFilter the predicate used to filter agents
     */
    public void addAgentFilter(Predicate<Agent> agentFilter) {
        agentFiltersCacheList.add(agentFilter);
    }

    /**
     * Retrieves a filtered set of agents using the cached filter.
     *
     * @param agentFilter the predicate used to filter agents
     * @return a filtered {@link MutableAgentSet}
     */
    public MutableAgentSet getFilteredAgents(Predicate<Agent> agentFilter) {
        return agentSetCache.getFilteredAgents(agentFilter);
    }

    /**
     * Checks if a specific agent exists in the cache.
     *
     * @param agentName the name of the agent
     * @return true if the agent is cached
     */
    public boolean doesAgentExist(String agentName) {
        return agentSetCache.doesAgentExist(agentName);
    }

    /**
     * Retrieves an agent from the cache by name.
     *
     * @param agentName the name of the agent
     * @return the cached {@link Agent}
     */
    public Agent getAgent(String agentName) {
        return agentSetCache.get(agentName);
    }

    /**
     * Adds a single agent to the cache.
     *
     * @param agent the agent to cache
     */
    public void addAgent(Agent agent) {
        agentSetCache.add(agent);
    }

    /**
     * Adds a full set of agents to the cache.
     *
     * @param agentSet the agent set to add
     */
    public void addAgents(MutableAgentSet agentSet) {
        agentSetCache.add(agentSet);
    }

    /**
     * Checks whether an environment instance has already been cached.
     *
     * @return true if an environment is stored
     */
    public boolean doesEnvironmentExist() {
        return environment != null;
    }

    /**
     * Returns the cached environment instance.
     *
     * @return the cached {@link Environment}
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Caches a reference to the environment for reuse within a tick.
     *
     * @param environment the environment instance to cache
     */
    public void addEnvironment(Environment environment) {
        this.environment = environment;
    }
}
