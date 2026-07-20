package modelarium.results.immutable;

import modelarium.results.Results;
import modelarium.results.ResultsForAgents;
import modelarium.results.ResultsForEnvironment;
import modelarium.results.mutable.MutableResults;

/**
 * Class for providing a read-only view of the results of a model run.
 *
 * <p>This class wraps the mutable results the model built up during its run so that the run's agent-level and
 * environment-level logs can be inspected without being modifiable.
 */
public final class ImmutableResults implements Results {

    /** The read-only view of the run's agent-level results */
    private final ImmutableResultsForAgents resultsForAgents;

    /** The read-only view of the run's environment-level results */
    private final ImmutableResultsForEnvironment resultsForEnvironment;

    /**
     * Constructs a new immutable results view of the specified mutable results.
     *
     * @param results the mutable results to provide a read-only view of
     */
    public ImmutableResults(MutableResults results) {
        resultsForAgents = results.agents().getAsImmutable();
        resultsForEnvironment = results.environment().getAsImmutable();
    }

    /**
     * Returns the agent-level results of the model run.
     *
     * @return the run's {@link ImmutableResultsForAgents} instance
     */
    @Override
    public ResultsForAgents agents() {
        return resultsForAgents;
    }

    /**
     * Returns the environment-level results of the model run.
     *
     * @return the run's {@link ImmutableResultsForEnvironment} instance
     */
    @Override
    public ResultsForEnvironment environment() {
        return resultsForEnvironment;
    }
}
