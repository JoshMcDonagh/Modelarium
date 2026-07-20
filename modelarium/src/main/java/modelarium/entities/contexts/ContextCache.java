package modelarium.entities.contexts;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.environments.Environment;
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
    private final IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgentsCache = new IdentityHashMap<>();

    /** The individually retrieved agents cached during the current tick */
    private final AgentSet individualAgentCache;

    /** The environment cached during the current tick, or null if it has not been retrieved */
    private Environment environment = null;

    /**
     * Constructs a new worker cache.
     */
    @Internal
    public ContextCache() {
        individualAgentCache = new AgentSet();
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
    public boolean doesAgentFilterExist(Predicate<Agent> agentFilter) {
        return filteredAgentsCache.containsKey(agentFilter);
    }

    /**
     * Caches the agents matching a filter.
     *
     * @param agentFilter the filter the agents were matched against
     * @param results the agents matching the filter
     */
    public void addFilteredAgents(Predicate<Agent> agentFilter, AgentSet results) {
        filteredAgentsCache.put(agentFilter, results);
    }

    /**
     * Retrieves the cached agents matching a filter.
     *
     * @param agentFilter the filter the agents were matched against
     * @return the cached {@link AgentSet} for the filter, or null if none is cached
     */
    public AgentSet getFilteredAgents(Predicate<Agent> agentFilter) {
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
    public Agent getAgent(String agentName) {
        return individualAgentCache.get(agentName);
    }

    /**
     * Caches an individually retrieved agent.
     *
     * @param agent the agent to cache
     */
    public void addAgent(Agent agent) {
        individualAgentCache.add(agent);
    }

    /**
     * Caches each agent in an agent set.
     *
     * @param agentSet the agents to cache
     */
    public void addAgents(AgentSet agentSet) {
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
     * @return the cached {@link Environment} instance, or null if none is cached
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Caches the environment.
     *
     * @param environment the environment to cache
     */
    public void addEnvironment(Environment environment) {
        this.environment = environment;
    }
}
