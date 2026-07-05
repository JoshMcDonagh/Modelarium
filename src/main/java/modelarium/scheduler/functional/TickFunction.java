package modelarium.scheduler.functional;

import modelarium.clock.ImmutableClock;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.immutable.ImmutableEnvironment;

import java.util.random.RandomGenerator;

@FunctionalInterface
public interface TickFunction {
    void runTick(
            ImmutableClock clock,
            ImmutableEnvironment environment,
            AgentSet agentSet,
            RandomGenerator randomGenerator
    );
}
