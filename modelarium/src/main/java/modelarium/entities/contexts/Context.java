package modelarium.entities.contexts;

import modelarium.clock.Clock;
import modelarium.entities.agents.immutable.ReadOnlyAgent;
import modelarium.entities.agents.immutable.ReadOnlyAgentSet;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.agents.mutable.AgentSet;

import java.util.List;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;

/**
 * Interface for providing model elements with access to shared simulation resources.
 *
 * <p>This interface exposes the model's clock and random generator along with read-only access to other agents,
 * whether those agents live on the same core or (in a synchronised model) on another core via the co-ordinator.
 */
public sealed interface Context permits SimulationContext, EntityContext {

    /**
     * Returns the model's clock.
     *
     * @return a read-only view of the model's {@link Clock}
     */
    Clock getClock();

    /**
     * Returns whether an agent with the given name exists on the current core.
     *
     * @param agentName the name of the agent to check for
     * @return true if the agent exists in this core's local agent set, false otherwise
     */
    boolean doesAgentExistInThisCore(String agentName);

    /**
     * Returns the model's current population size.
     *
     * @return the current population size as an int
     */
    int getCurrentPopulationSize();

    /**
     * Retrieves an agent by name.
     *
     * @param targetAgentName the name of the agent to retrieve
     * @return a read-only view of the requested agent
     */
    ReadOnlyAgent getAgent(String targetAgentName);

    /**
     * Retrieves the agents matching a filter.
     *
     * @param filter a predicate to apply to each agent
     * @return a read-only view of the matching agents
     */
    ReadOnlyAgentSet getFilteredAgents(Predicate<ReadOnlyAgent> filter);

    /**
     * Returns the random generator this model element can use.
     *
     * @return the element's {@link RandomGenerator} instance
     */
    RandomGenerator getRandom();

    /**
     * Adds an agent to the current core's local agent set, creating the context the agent needs to run.
     *
     * @param agent the agent to add
     */
    void addAgent(Agent agent);

    /**
     * Adds each agent in an agent set to the current core's local agent set, creating the contexts the agents need
     * to run.
     *
     * @param agentSet the agents to add
     */
    void addAgents(AgentSet agentSet);

    /**
     * Adds each agent in a list to the current core's local agent set, creating the contexts the agents need to run.
     *
     * @param agentList the agents to add
     */
    void addAgents(List<Agent> agentList);

    /**
     * Kills the agent with the given name.
     *
     * @param agentName the agent's name as a string
     */
    void killAgent(String agentName);

    /**
     * Kills the agent of the given {@link ReadOnlyAgent} instance.
     *
     * @param agent the agent to kill
     */
    void killAgent(ReadOnlyAgent agent);

    /**
     * Kills all the agents with names in a given {@link List<String>} instance.
     *
     * @param agentNames the list of names of agents to kill
     */
    void killAgents(List<String> agentNames);

    /**
     * Kills all the agents in a given {@link ReadOnlyAgentSet} instance.
     *
     * @param agentSet the agent set of agents to kill
     */
    void killAgents(ReadOnlyAgentSet agentSet);
}
