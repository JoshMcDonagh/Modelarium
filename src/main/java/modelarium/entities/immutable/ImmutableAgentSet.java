package modelarium.entities.immutable;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public final class ImmutableAgentSet implements Iterable<ImmutableAgent> {
    private final AgentSet agentSet;

    public ImmutableAgentSet(AgentSet agentSet) {
        this.agentSet = agentSet;
    }

    public ImmutableAgent get(String agentName) {
        return new ImmutableAgent(agentSet.get(agentName));
    }

    public ImmutableAgent get(int index) {
        return new ImmutableAgent(agentSet.get(index));
    }

    public List<ImmutableAgent> getAsList() {
        List<Agent> originalList = agentSet.getAsList();
        List<ImmutableAgent> newList = new ArrayList<>();

        for (Agent agent : originalList)
            newList.add(new ImmutableAgent(agent));

        return newList;
    }

    public int size() {
        return agentSet.size();
    }

    public boolean isEmpty() {
        return agentSet.isEmpty();
    }

    public boolean doesAgentExist(String agentName) {
        return agentSet.doesAgentExist(agentName);
    }

    public ImmutableAgentSet getFilteredAgents(Predicate<Agent> agentFilter) {
        return agentSet.getFilteredAgents(agentFilter).getAsImmutable();
    }

    public Iterator<ImmutableAgent> getRandomIterator() {
        List<ImmutableAgent> shuffledAgents = getAsList();
        Collections.shuffle(shuffledAgents);
        return shuffledAgents.iterator();
    }

    @Override
    public Iterator<ImmutableAgent> iterator() {
        return getAsList().iterator();
    }
}
