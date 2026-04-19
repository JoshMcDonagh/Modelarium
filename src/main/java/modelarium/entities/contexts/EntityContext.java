package modelarium.entities.contexts;

import modelarium.clock.Clock;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.ImmutableAgentSet;

import java.util.function.Predicate;

public sealed interface EntityContext extends Context permits AgentContext, EnvironmentContext {
    Clock getClock();
    boolean doesAgentExistInThisCore(String agentName);
    Agent getAgent(String targetAgentName);
    ImmutableAgentSet getFilteredAgents(Predicate<Agent> filter);
}
