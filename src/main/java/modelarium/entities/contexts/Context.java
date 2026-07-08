package modelarium.entities.contexts;

import modelarium.clock.Clock;
import modelarium.entities.agents.Agent;
import modelarium.entities.immutable.ImmutableAgent;
import modelarium.entities.immutable.ImmutableAgentSet;

import java.util.function.Predicate;
import java.util.random.RandomGenerator;

public sealed interface Context permits SimulationContext, EntityContext {
    Clock getClock();
    boolean doesAgentExistInThisCore(String agentName);
    ImmutableAgent getAgent(String targetAgentName);
    ImmutableAgentSet getFilteredAgents(Predicate<Agent> filter);
    RandomGenerator getRandom();
}
