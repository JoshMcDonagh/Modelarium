package modelarium.scheduler.functional;

import modelarium.clock.ReadOnlyClock;
import modelarium.entities.agentsets.AgentSet;
import modelarium.entities.readonly.ReadOnlyEnvironment;
import modelarium.scheduler.Scheduler;

import java.util.random.RandomGenerator;

/**
 * Class for scheduling agents using a user-defined function to run each simulation tick.
 *
 * <p>This class allows flexible tick behaviour without requiring subclassing or direct implementation of the
 * {@link Scheduler} interface. The user provides a {@link TickFunction} that defines what should happen during a
 * single simulation tick for a given set of agents.
 *
 * <p>Typical use cases include passing a lambda expression or method reference to customise the tick logic
 * externally, for example from another Java module or from Python via JPype with a proxy.
 */
public class FunctionalScheduler implements Scheduler {

    /** The function to be executed on each tick */
    private final TickFunction tickFunction;

    /**
     * Constructs a new functional scheduler with the specified tick function.
     *
     * @param tickFunction the logic to execute on each tick
     */
    public FunctionalScheduler(TickFunction tickFunction) {
        this.tickFunction = tickFunction;
    }

    /**
     * Executes a single simulation tick by delegating to the user-provided function.
     *
     * @param threadName the name of the worker thread running the tick
     * @param clock the model's clock, giving the current tick
     * @param environment a read-only view of the worker's environment
     * @param agentSet the set of agents to act upon during this tick
     * @param random the random generator the tick logic can use
     */
    @Override
    public void runTick(
            String threadName,
            ReadOnlyClock clock,
            ReadOnlyEnvironment environment,
            AgentSet agentSet,
            RandomGenerator random
    ) {
        tickFunction.runTick(threadName, clock, environment, agentSet, random);
    }
}
