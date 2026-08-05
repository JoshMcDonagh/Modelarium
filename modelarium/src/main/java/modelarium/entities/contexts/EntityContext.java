package modelarium.entities.contexts;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;

import java.util.List;

/**
 * Interface for marking a context as belonging to an entity.
 *
 * <p>This interface is extended by the entity-flavoured context interfaces: {@link AgentContext} and
 * {@link EnvironmentContext}.
 */
public sealed interface EntityContext extends Context permits AgentContext, EnvironmentContext {
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
}
