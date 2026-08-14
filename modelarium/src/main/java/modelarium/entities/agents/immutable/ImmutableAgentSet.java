package modelarium.entities.agents.immutable;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.agents.mutable.MutableAgentSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;

/**
 * Class for providing a read-only view of an {@link MutableAgentSet}.
 *
 * <p>This class wraps a mutable agent set so that its agents can be looked up, filtered and iterated over without
 * the underlying set being modifiable, with each retrieved agent wrapped as an {@link ImmutableAgent}.
 */
public final class ImmutableAgentSet implements AgentSet<ImmutableAgent, ImmutableAgentSet> {

    /** The mutable agent set this read-only view wraps */
    private final MutableAgentSet agentSet;

    /**
     * Constructs a new immutable agent set wrapping the specified mutable agent set.
     *
     * @param agentSet the mutable agent set to provide a read-only view of
     */
    public ImmutableAgentSet(MutableAgentSet agentSet) {
        this.agentSet = agentSet;
    }

    /**
     * Retrieves an agent by name.
     *
     * @param agentName the agent's unique name
     * @return a read-only view of the agent with the specified name
     */
    public ImmutableAgent get(String agentName) {
        return new ImmutableAgent(agentSet.get(agentName));
    }

    /**
     * Retrieves an agent by index.
     *
     * @param index the index of the agent
     * @return a read-only view of the agent at the given position
     */
    public ImmutableAgent get(int index) {
        return new ImmutableAgent(agentSet.get(index));
    }

    /**
     * Returns the agents in this set as a list of read-only views.
     *
     * @return a list of {@link ImmutableAgent} instances
     */
    public List<ImmutableAgent> getAsList() {
        List<MutableAgent> originalList = agentSet.getAsList();
        List<ImmutableAgent> newList = new ArrayList<>();

        for (MutableAgent agent : originalList)
            newList.add(new ImmutableAgent(agent));

        return newList;
    }

    /**
     * Returns the number of agents in the set.
     *
     * @return the size of the agent set
     */
    public int size() {
        return agentSet.size();
    }

    /**
     * Returns whether the set contains no agents.
     *
     * @return true if the set is empty, false otherwise
     */
    public boolean isEmpty() {
        return agentSet.isEmpty();
    }

    /**
     * Checks if an agent exists in the set by name.
     *
     * @param agentName the name to check
     * @return true if the agent exists
     */
    public boolean doesAgentExist(String agentName) {
        return agentSet.doesAgentExist(agentName);
    }

    /**
     * Returns a filtered view of the agent set.
     *
     * @param agentFilter a predicate to apply to each agent
     * @return a new {@link ImmutableAgentSet} containing only matching agents
     */
    public ImmutableAgentSet getFilteredAgents(Predicate<Agent> agentFilter) {
        return agentSet.getFilteredAgents(agentFilter).getAsImmutable();
    }

    /**
     * Returns a randomised iterator over the agents in this set.
     *
     * @param randomGenerator the random generator used to shuffle the agents
     * @return an iterator that yields read-only views of the agents in random order
     */
    public Iterator<ImmutableAgent> getRandomIterator(RandomGenerator randomGenerator) {
        List<ImmutableAgent> shuffledAgents = getAsList();
        Collections.shuffle(shuffledAgents, randomGenerator);
        return shuffledAgents.iterator();
    }

    /**
     * Standard iterator over the agents in the order they were added.
     *
     * @return an iterator over read-only views of the agents
     */
    @Override
    public Iterator<ImmutableAgent> iterator() {
        return getAsList().iterator();
    }
}
