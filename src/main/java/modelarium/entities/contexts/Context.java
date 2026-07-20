package modelarium.entities.contexts;

import modelarium.clock.Clock;
import modelarium.entities.agents.Agent;
import modelarium.entities.immutable.ImmutableAgent;
import modelarium.entities.immutable.ImmutableAgentSet;

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
     * Retrieves an agent by name, whether it lives on this core or (in a synchronised model) on another core.
     *
     * @param targetAgentName the name of the agent to retrieve
     * @return a read-only view of the requested agent
     */
    ImmutableAgent getAgent(String targetAgentName);

    /**
     * Retrieves the agents matching a filter, drawn from the whole population in a synchronised model or from this
     * core's local agents otherwise.
     *
     * @param filter a predicate to apply to each agent
     * @return a read-only view of the matching agents
     */
    ImmutableAgentSet getFilteredAgents(Predicate<Agent> filter);

    /**
     * Returns the random generator this model element can use.
     *
     * @return the element's {@link RandomGenerator} instance
     */
    RandomGenerator getRandom();
}
