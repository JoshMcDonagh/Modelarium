package modelarium.entities.contexts;

import modelarium.clock.Clock;

import java.util.random.RandomGenerator;

public sealed interface Context permits SimulationContext, EntityContext {
    Clock getClock();
    boolean doesAgentExistInThisCore(String agentName);
    RandomGenerator getRandom();
}
