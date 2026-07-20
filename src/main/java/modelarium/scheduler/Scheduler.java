package modelarium.scheduler;

import modelarium.clock.ImmutableClock;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.immutable.ImmutableEnvironment;

import java.util.random.RandomGenerator;

/**
 * Interface for representing a scheduling policy for running a single tick of the agent-based model.
 *
 * <p>Implementations of this interface define the order in which a tick is executed over a given set of agents.
 */
public interface Scheduler {

    /**
     * Executes a single simulation tick for the provided agent set.
     *
     * @param threadName the name of the worker thread running the tick
     * @param clock the model's clock, giving the current tick
     * @param environment a read-only view of the worker's environment
     * @param agentSet the set of agents to execute for this tick
     * @param randomGenerator the random generator the scheduler can use for ordering decisions
     */
    void runTick(
            String threadName,
            ImmutableClock clock,
            ImmutableEnvironment environment,
            AgentSet agentSet,
            RandomGenerator randomGenerator
    );
}
