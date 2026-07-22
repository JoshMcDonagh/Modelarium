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
    /** The mutable results associated with this immutable results */
    private final MutableResults mutableVersion;

    /**
     * Constructs a new immutable results view of the specified mutable results.
     *
     * @param results the mutable results to provide a read-only view of
     */
    public ImmutableResults(MutableResults results) {
        mutableVersion = results;
    }

    /**
     * Returns the agent-level results of the model run.
     *
     * @return the run's {@link ImmutableResultsForAgents} instance
     */
    @Override
    public ResultsForAgents agents() {
        return mutableVersion.agents().getAsImmutable();
    }

    /**
     * Returns the environment-level results of the model run.
     *
     * @return the run's {@link ImmutableResultsForEnvironment} instance
     */
    @Override
    public ResultsForEnvironment environment() {
        return mutableVersion.environment().getAsImmutable();
    }

    /**
     * Exports the results of the model to a given export directory
     *
     * @param exportDir the directory to export the results to
     */
    @Override
    public void export(String exportDir) {
        mutableVersion.export(exportDir);
    }
}
