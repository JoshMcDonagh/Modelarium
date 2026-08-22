package modelarium.scheduler;

import modelarium.clock.ImmutableClock;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.agents.mutable.AgentSet;
import modelarium.entities.environments.ReadOnlyEnvironment;

import java.util.random.RandomGenerator;

/**
 * Class for scheduling agents to execute in the order they appear in the agent set.
 *
 * <p>This scheduling strategy ensures that all agents are processed sequentially and predictably for each
 * simulation tick.
 */
public class InOrderScheduler implements Scheduler {

    /**
     * Executes each agent's {@code run()} method in the order they are stored in the agent set.
     *
     * @param threadName the name of the worker thread running the tick
     * @param clock the model's clock, giving the current tick
     * @param environment a read-only view of the worker's environment
     * @param agentSet the set of agents to run for this tick
     * @param random the random generator the scheduler can use for ordering decisions
     */
    @Override
    public void runTick(
            String threadName,
            ImmutableClock clock,
            ReadOnlyEnvironment environment,
            AgentSet agentSet,
            RandomGenerator random
    ) {
        for (Agent agent : agentSet)
            agent.run();
    }
}
