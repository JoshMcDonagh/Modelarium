package modelarium.entities.agents.sets;

import modelarium.entities.agents.Agent;

import java.util.List;
import java.util.function.Predicate;

public sealed interface AgentSet extends Iterable<Agent> permits MutableAgentSet, ImmutableAgentSet {
    Agent get(String agentName);
    Agent get(int index);
    List<Agent> getAsList();
    int size();
    boolean isEmpty();
    boolean doesAgentExist(String agentName);
    AgentSet getFilteredAgents(Predicate<Agent> agentFilter);
}
