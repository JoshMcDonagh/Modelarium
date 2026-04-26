package modelarium.entities.contexts;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.environments.Environment;
import modelarium.internal.Internal;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Predicate;

public class ContextCache {
    /** List of previously applied agent filters (for caching filtered sets) */
    private final IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgentsCache = new IdentityHashMap<>();

    private final AgentSet individualAgentCache;

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

    public boolean doesAgentFilterExist(Predicate<Agent> agentFilter) {
        return filteredAgentsCache.containsKey(agentFilter);
    }

    public void addFilteredAgents(Predicate<Agent> agentFilter, AgentSet results) {
        filteredAgentsCache.put(agentFilter, results);
    }

    public AgentSet getFilteredAgents(Predicate<Agent> agentFilter) {
        return filteredAgentsCache.get(agentFilter);
    }

    public boolean doesAgentExist(String agentName) {
        return individualAgentCache.doesAgentExist(agentName);
    }

    public Agent getAgent(String agentName) {
        return individualAgentCache.get(agentName);
    }

    public void addAgent(Agent agent) {
        individualAgentCache.add(agent);
    }

    public void addAgents(AgentSet agentSet) {
        individualAgentCache.add(agentSet);
    }

    public boolean doesEnvironmentExist() {
        return environment != null;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void addEnvironment(Environment environment) {
        this.environment = environment;
    }
}
