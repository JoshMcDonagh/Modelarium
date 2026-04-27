package modelarium.scheduler;

import modelarium.clock.ImmutableClock;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.immutable.ImmutableEnvironment;

import java.util.Iterator;

/**
 * A scheduler that executes agents in a randomised order for each tick.
 * <p>
 * This scheduling strategy helps reduce bias introduced by fixed execution
 * orders and may more closely reflect stochastic processes in real-world systems.
 * </p>
 */
public class RandomOrderScheduler implements Scheduler {

    /**
     * Executes each agent's {@code run()} method in a randomised order.
     *
     * @param agentSet the set of agents to run for this tick
     */
    @Override
    public void runTick(ImmutableClock clock, ImmutableEnvironment environment, AgentSet agentSet) {
        Iterator<Agent> randomIterator = agentSet.getRandomIterator();
        while (randomIterator.hasNext())
            randomIterator.next().run();
    }
}
