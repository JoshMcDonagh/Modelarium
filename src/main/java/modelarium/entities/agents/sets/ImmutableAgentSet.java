package modelarium.entities.agents.sets;

import modelarium.entities.agents.Agent;

import java.util.List;

public class ImmutableAgentSet extends AgentSet {
    ImmutableAgentSet(AgentSet mutableAgentSet) {
        super();
        super.add(mutableAgentSet);
    }

    private void throwUnsupportedOperationException() {
        throw new UnsupportedOperationException("ImmutableAgentSet cannot be modified");
    }

    @Override
    public void add(Agent agent) {
        throwUnsupportedOperationException();
    }

    @Override
    public void add(List<Agent> agents) {
        throwUnsupportedOperationException();
    }

    @Override
    public void add(AgentSet agentSet) {
        throwUnsupportedOperationException();
    }

    @Override
    public void clear() {
        throwUnsupportedOperationException();
    }

    @Override
    public void update(AgentSet otherAgentSet) {
        throwUnsupportedOperationException();
    }

    @Override
    public ImmutableAgentSet getAsImmutable() {
        throw new UnsupportedOperationException("ImmutableAgentSet is already immutable");
    }

    @Override
    public ImmutableAgentSet duplicate() {
        return new ImmutableAgentSet(super.duplicate());
    }
}
