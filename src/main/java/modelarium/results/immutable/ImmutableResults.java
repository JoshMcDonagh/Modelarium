package modelarium.results.immutable;

import modelarium.results.Results;
import modelarium.results.ResultsForAgents;
import modelarium.results.ResultsForEnvironment;
import modelarium.results.mutable.MutableResults;

public final class ImmutableResults implements Results {
    private final ImmutableResultsForAgents resultsForAgents;
    private final ImmutableResultsForEnvironment resultsForEnvironment;

    public ImmutableResults(MutableResults results) {
        resultsForAgents = results.agents().getAsImmutable();
        resultsForEnvironment = results.environment().getAsImmutable();
    }

    @Override
    public ResultsForAgents agents() {
        return resultsForAgents;
    }

    @Override
    public ResultsForEnvironment environment() {
        return resultsForEnvironment;
    }
}
