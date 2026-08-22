package modelarium.scheduler;

import modelarium.clock.ImmutableClock;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.agents.mutable.AgentSet;
import modelarium.entities.environments.ReadOnlyEnvironment;

import java.util.Iterator;
import java.util.random.RandomGenerator;

/**
 * Class for scheduling agents to execute in a randomised order for each tick.
 *
 * <p>This scheduling strategy helps reduce bias introduced by fixed execution orders and may more closely reflect
 * stochastic processes in real-world systems.
 */
public class RandomOrderScheduler implements Scheduler {

    /**
     * Executes each agent's {@code run()} method in a randomised order.
     *
     * @param threadName the name of the worker thread running the tick
     * @param clock the model's clock, giving the current tick
     * @param environment a read-only view of the worker's environment
     * @param agentSet the set of agents to run for this tick
     * @param random the random generator used to shuffle the agents
     */
    @Override
    public void runTick(
            String threadName,
            ImmutableClock clock,
            ReadOnlyEnvironment environment,
            AgentSet agentSet,
            RandomGenerator random
    ) {
        Iterator<Agent> randomIterator = agentSet.getRandomIterator(random);
        while (randomIterator.hasNext())
            randomIterator.next().run();
    }
}
