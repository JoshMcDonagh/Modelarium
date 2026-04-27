package modelarium.entities.agents;

import com.rits.cloning.Cloner;
import modelarium.entities.immutable.ImmutableAgentSet;
import modelarium.entities.logging.databases.factories.AttributeSetLogDatabaseFactory;
import modelarium.exceptions.AgentNotFoundException;
import modelarium.internal.Internal;

import java.util.*;
import java.util.function.Predicate;

/**
 * A collection class for managing {@link Agent} instances, with support for:
 * <ul>
 *     <li>Optional deep copying of agents on insertion</li>
 *     <li>Fast lookup by agent name</li>
 *     <li>Filtering, duplication, and setup routines</li>
 *     <li>Randomised iteration</li>
 * </ul>
 *
 * <p>This class is iterable and designed to support both sequential and parallel simulation use cases.
 */
public final class AgentSet implements Iterable<Agent> {
    private static final Cloner cloner = new Cloner();

    /** Ordered list of agents in the set */
    private List<Agent> agentList = new ArrayList<>();

    /** Map from agent names to their index in the list */
    private Map<String, Integer> agentIndexMap = new HashMap<>();

    /**
     * Constructs a new agent set from a list of agents, without deep copying.
     *
     * @param agentsList list of agents to add
     */
    public AgentSet(List<Agent> agentsList) {
        add(agentsList);
    }

    /** Constructs an empty agent set without deep copying. */
    public AgentSet() {}

    @Internal
    public void setLogDatabaseFactory(AttributeSetLogDatabaseFactory databaseFactory) {
        for (Agent agent : agentList)
            agent.setLogDatabaseFactory(databaseFactory);
    }

    private void addExistingAgent(Agent agent, boolean isDeepCopied) {
        int index = agentIndexMap.get(agent.name());
        if (isDeepCopied)
            agentList.set(index, cloner.deepClone(agent));
        else
            agentList.set(index, agent);
    }

    private void addNewAgent(Agent agent, boolean isDeepCopied) {
        int index = agentList.size();
        agentIndexMap.put(agent.name(), index);
        if (isDeepCopied)
            agentList.add(cloner.deepClone(agent));
        else
            agentList.add(agent);
    }

    /**
     * Adds an agent to the set. If the agent already exists, it will be replaced.
     *
     * @param agent the agent to add
     */
    public void add(Agent agent) {
        if (doesAgentExist(agent.name()))
            addExistingAgent(agent, false);
        else
            addNewAgent(agent, false);
    }

    /**
     * Adds a list of agents to the set.
     *
     * @param agents list of agents to add
     */
    public void add(List<Agent> agents) {
        for (Agent agent : agents)
            add(agent);
    }

    /**
     * Adds all agents from another {@link AgentSet} that do not already exist in this set.
     * Existing agents (by name) are not modified or replaced.
     *
     * @param agentSet the agent set to add from
     */
    public void add(AgentSet agentSet) {
        if (agentSet == null)
            throw new IllegalArgumentException("agentSet cannot be null");

        for (Agent agent : agentSet) {
            if (!doesAgentExist(agent.name()))
                add(agent);
        }
    }

    public void addDeepCopy(Agent agent) {
        if (doesAgentExist(agent.name()))
            addExistingAgent(agent, true);
        else
            addNewAgent(agent, true);
    }

    public void addDeepCopy(List<Agent> agents) {
        for (Agent agent : agents)
            addDeepCopy(agent);
    }

    public void addDeepCopy(AgentSet agentSet) {
        if (agentSet == null)
            throw new IllegalArgumentException("agentSet cannot be null");

        for (Agent agent : agentSet) {
            if (!doesAgentExist(agent.name()))
                addDeepCopy(agent);
        }
    }

    /**
     * Retrieves an agent by name.
     *
     * @param agentName the agent's unique name
     * @return the agent instance
     */
    public Agent get(String agentName) {
        Integer index = agentIndexMap.get(agentName);

        if (index == null)
            throw new AgentNotFoundException("No agent named '" + agentName + "' in set");

        return agentList.get(index);
    }

    /**
     * Retrieves an agent by index.
     *
     * @param index the index of the agent
     * @return the agent at the given position
     */
    public Agent get(int index) {
        return agentList.get(index);
    }

    /**
     * Returns the list of agents in this set.
     *
     * @return a list of agent instances
     */
    public List<Agent> getAsList() {
        return new ArrayList<>(agentList);
    }

    /**
     * Returns the number of agents in the set.
     *
     * @return the size of the agent set
     */
    public int size() {
        return agentList.size();
    }

    public boolean isEmpty() {
        return agentList.isEmpty();
    }

    /**
     * Clears the agent set entirely.
     */
    public void clear() {
        agentIndexMap = new HashMap<>();
        agentList = new ArrayList<>();
    }

    /**
     * Checks if an agent exists in the set by name.
     *
     * @param agentName the name to check
     * @return true if the agent exists
     */
    public boolean doesAgentExist(String agentName) {
        return agentIndexMap.containsKey(agentName);
    }

    /**
     * Updates this set with all agents from another set.
     * Existing agents are replaced if names match.
     *
     * @param otherAgentSet the other agent set to pull from
     */
    public void update(AgentSet otherAgentSet, boolean areDeepCopied) {
        if (otherAgentSet == null)
            throw new IllegalArgumentException("otherAgentSet cannot be null");

        for (int i = 0; i < otherAgentSet.size(); i++) {
            Agent agent = otherAgentSet.get(i);
            if (areDeepCopied)
                addDeepCopy(agent);
            else
                add(agent);
        }
    }

    /**
     * Returns a filtered view of the agent set.
     *
     * @param agentFilter a predicate to apply to each agent
     * @return a new {@code AgentSet} containing only matching agents
     */
    public AgentSet getFilteredAgents(Predicate<Agent> agentFilter) {
        List<Agent> filteredAgents = new ArrayList<>();

        for (Agent agent : agentList) {
            if (agentFilter.test(agent))
                filteredAgents.add(agent);
        }

        return new AgentSet(filteredAgents);
    }

    /**
     * Returns a randomised iterator over the agents in this set.
     *
     * @return an iterator that yields agents in random order
     */
    public Iterator<Agent> getRandomIterator() {
        List<Agent> shuffledAgents = new ArrayList<>(agentList);
        Collections.shuffle(shuffledAgents);
        return shuffledAgents.iterator();
    }

    public ImmutableAgentSet getAsImmutable() {
        return new ImmutableAgentSet(this);
    }

    /**
     * Returns a duplicate of this agent set.
     * If deep copy is enabled, agents will be duplicated as well.
     *
     * @return a new {@code AgentSet} with the same agents
     */
    public AgentSet duplicate() {
        return new AgentSet(agentList);
    }

    /**
     * Standard iterator over the agents in the order they were added.
     *
     * @return an iterator over the agent list
     */
    @Override
    public Iterator<Agent> iterator() {
        return agentList.iterator();
    }
}
