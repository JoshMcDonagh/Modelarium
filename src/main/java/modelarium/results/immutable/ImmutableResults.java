package modelarium.results.immutable;

import modelarium.entities.agents.sets.MutableAgentSet;
import modelarium.results.Results;
import modelarium.results.ResultsForAgents;
import modelarium.results.ResultsForEnvironment;

import java.util.List;

public class ImmutableResults extends Results {
    public ImmutableResults(Results mutableResults) {
        super(mutableResults);
    }

    private void throwUnsupportedOperationException() {
        throw new UnsupportedOperationException("ImmutableResults cannot be modified");
    }

    @Override
    public void setAgentNames(MutableAgentSet agents) {
        throwUnsupportedOperationException();
    }

    @Override
    public void setAgentNames(List<MutableAgentSet> agentSetList) {
        throwUnsupportedOperationException();
    }

    @Override
    public void setAgentResults(ResultsForAgents agentsResults) {
        throwUnsupportedOperationException();
    }

    @Override
    public void setEnvironmentResults(ResultsForEnvironment environmentResults) {
        throwUnsupportedOperationException();
    }

    @Override
    public void mergeWith(Results other) {
        throwUnsupportedOperationException();
    }

    @Override
    public ImmutableResultsForAgents agents() {
        return super.agents().getAsImmutable();
    }

    @Override
    public ImmutableResultsForEnvironment environment() {
        return super.environment().getAsImmutable();
    }
}
