package modelarium.scheduler.functional;

import modelarium.clock.ImmutableClock;
import modelarium.entities.agents.mutable.AgentSet;
import modelarium.entities.environments.ReadOnlyEnvironment;

import java.util.random.RandomGenerator;

/**
 * Functional interface for defining how a functional scheduler runs a single tick over a set of agents.
 */
@FunctionalInterface
public interface TickFunction {

    /**
     * Executes a single simulation tick for the provided agent set.
     *
     * @param threadName the name of the worker thread running the tick
     * @param clock the model's clock, giving the current tick
     * @param environment a read-only view of the worker's environment
     * @param agentSet the set of agents to execute for this tick
     * @param randomGenerator the random generator the tick logic can use
     */
    void runTick(
            String threadName,
            ImmutableClock clock,
            ReadOnlyEnvironment environment,
            AgentSet agentSet,
            RandomGenerator randomGenerator
    );
}
