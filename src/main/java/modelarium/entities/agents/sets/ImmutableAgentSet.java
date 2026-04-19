package modelarium.entities.agents.sets;

import com.rits.cloning.Cloner;
import modelarium.entities.agents.Agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public final class ImmutableAgentSet implements AgentSet {
    private static final Cloner cloner = new Cloner();

    private final MutableAgentSet agentSet;

    ImmutableAgentSet(MutableAgentSet mutableAgentSet) {
        agentSet = mutableAgentSet;
    }

    @Override
    public Agent get(String agentName) {
        return cloner.deepClone(agentSet.get(agentName));
    }

    @Override
    public Agent get(int index) {
        return cloner.deepClone(agentSet.get(index));
    }

    @Override
    public List<Agent> getAsList() {
        return cloner.deepClone(agentSet.getAsList());
    }

    @Override
    public int size() {
        return agentSet.size();
    }

    @Override
    public boolean isEmpty() {
        return agentSet.isEmpty();
    }

    @Override
    public boolean doesAgentExist(String agentName) {
        return agentSet.doesAgentExist(agentName);
    }

    @Override
    public ImmutableAgentSet getFilteredAgents(Predicate<Agent> agentFilter) {
        return agentSet.getFilteredAgents(agentFilter).getAsImmutable();
    }

    @Override
    public Iterator<Agent> getRandomIterator() {
        List<Agent> shuffledAgents = getAsList();
        Collections.shuffle(shuffledAgents);
        return shuffledAgents.iterator();
    }

    @Override
    public Iterator<Agent> iterator() {
        return getAsList().iterator();
    }
}
