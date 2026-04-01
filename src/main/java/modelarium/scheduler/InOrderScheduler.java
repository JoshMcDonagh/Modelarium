package modelarium.scheduler;

import modelarium.agents.Agent;
import modelarium.agents.sets.AgentSet;

/**
 * A scheduler that executes each agent in the order they appear in the agent set.
 * <p>
 * This scheduling strategy ensures that all agents are processed sequentially
 * and predictably for each simulation tick.
 * </p>
 */
public class InOrderScheduler implements ModelScheduler {

    /**
     * Executes each agent's {@code run()} method in the order they are stored in the agent set.
     *
     * @param agentSet the set of agents to run for this tick
     */
    @Override
    public void runTick(AgentSet agentSet) {
        for (Agent agent : agentSet)
            agent.run();
    }
}
