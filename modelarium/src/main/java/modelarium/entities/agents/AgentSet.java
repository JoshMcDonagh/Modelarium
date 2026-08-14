package modelarium.entities.agents;

import modelarium.entities.agents.immutable.ImmutableAgentSet;
import modelarium.entities.agents.mutable.MutableAgentSet;

import java.util.List;
import java.util.function.Predicate;

/**
 * An interface to access a stored set of {@link Agent} instances.
 *
 * @param <A> the type of agent stored
 * @param <AS> the type of agent set this is
 */
public sealed interface AgentSet<A extends Agent, AS extends AgentSet<A, AS>> extends Iterable<A> permits MutableAgentSet, ImmutableAgentSet {
    /**
     * Retrieves an agent by index.
     *
     * @param index the index of the agent
     * @return the agent at the given position
     */
    A get(int index);

    /**
     * Retrieves an agent by name.
     *
     * @param agentName the agent's unique name
     * @return the agent instance
     */
    A get(String agentName);

    /**
     * Returns the list of agents in this set.
     *
     * @return a list of agent instances
     */
    List<A> getAsList();

    /**
     * Returns the number of agents in the set.
     *
     * @return the size of the agent set
     */
    int size();

    /**
     * Returns whether the set contains no agents.
     *
     * @return true if the set is empty, false otherwise
     */
    boolean isEmpty();

    /**
     * Checks if an agent exists in the set by name.
     *
     * @param agentName the name to check
     * @return true if the agent exists
     */
    boolean doesAgentExist(String agentName);

    /**
     * Returns a filtered view of the agent set.
     *
     * @param agentFilter a predicate to apply to each agent
     * @return a new {@link AgentSet} containing only matching agents
     */
    AS getFilteredAgents(Predicate<Agent> agentFilter);
}
