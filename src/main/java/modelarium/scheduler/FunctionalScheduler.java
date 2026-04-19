package modelarium.scheduler;

import modelarium.entities.agents.sets.AgentSet;

import java.util.function.Consumer;

/**
 * A scheduler implementation that delegates each simulation tick
 * to a user-defined function.
 *
 * <p>This allows flexible tick behaviour without requiring subclassing
 * or direct implementation of the {@link Scheduler} interface.
 * The user provides a {@link Consumer} that defines what should happen
 * during a single simulation tick for a given set of agents.</p>
 *
 * <p>Typical use cases include passing a lambda expression or method reference
 * to customise the tick logic externally, for example from another Java module
 * or from Python via JPype with a proxy.</p>
 */
public class FunctionalScheduler implements Scheduler {

    /** The function to be executed on each tick, taking an AgentSet as input */
    private final Consumer<AgentSet> tickFunction;

    /**
     * Constructs a FunctionalScheduler with the specified tick function.
     *
     * @param tickFunction the logic to execute on each tick,
     *                     defined as a {@link Consumer} of {@link AgentSet}
     */
    public FunctionalScheduler(Consumer<AgentSet> tickFunction) {
        this.tickFunction = tickFunction;
    }

    /**
     * Executes a single simulation tick by delegating to the user-provided function.
     *
     * @param agentSet the set of agents to act upon during this tick
     */
    @Override
    public void runTick(AgentSet agentSet) {
        tickFunction.accept(agentSet);
    }
}
