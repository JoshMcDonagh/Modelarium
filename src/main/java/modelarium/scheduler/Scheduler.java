package modelarium.scheduler;

import modelarium.clock.ImmutableClock;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.immutable.ImmutableEnvironment;

import java.util.random.RandomGenerator;

/**
 * Abstract class representing a scheduling policy for running a single tick
 * of the agent-based model.
 * <p>
 * Implementations of this abstract class define how a tick is executed over
 * a given set of agents.
 * </p>
 */
public abstract class Scheduler {
    private RandomGenerator randomGenerator;

    public void setRandomGenerator(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    protected RandomGenerator getRandomGenerator() {
        return randomGenerator;
    }

    /**
     * Executes a single simulation tick for the provided agent set.
     *
     * @param agentSet the set of agents to execute for this tick
     */
    public abstract void runTick(ImmutableClock clock, ImmutableEnvironment environment, AgentSet agentSet);
}
