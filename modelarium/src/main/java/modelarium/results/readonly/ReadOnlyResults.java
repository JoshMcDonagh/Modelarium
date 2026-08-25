package modelarium.results.readonly;

import modelarium.results.Results;

import java.nio.file.Path;

/**
 * Class for providing a read-only view of the results of a model run.
 *
 * <p>This class wraps the mutable results the model built up during its run so that the run's agent-level and
 * environment-level logs can be inspected without being modifiable.
 */
public final class ReadOnlyResults {
    /** The mutable results associated with this immutable results */
    private final Results mutableVersion;

    /**
     * Constructs a new immutable results view of the specified mutable results.
     *
     * @param results the mutable results to provide a read-only view of
     */
    public ReadOnlyResults(Results results) {
        mutableVersion = results;
    }

    /**
     * Returns the agent-level results of the model run.
     *
     * @return the run's {@link ReadOnlyResultsForAgents} instance
     */
    public ReadOnlyResultsForAgents agents() {
        return mutableVersion.agents().getAsImmutable();
    }

    /**
     * Returns the environment-level results of the model run.
     *
     * @return the run's {@link ReadOnlyResultsForEnvironment} instance
     */
    public ReadOnlyResultsForEnvironment environment() {
        return mutableVersion.environment().getAsImmutable();
    }

    /**
     * Exports the results of the model to a given export directory
     *
     * @param exportDir the directory to export the results to
     * @return the {@link Path} directory containing the exported results
     */
    public Path export(String exportDir) {
        return mutableVersion.export(exportDir);
    }

    /**
     * Exports the results of the model to a given export directory given as a {@link Path}
     *
     * @param exportPath the path directory to export the results to
     * @return the {@link Path} directory containing the exported results
     */
    public Path export(Path exportPath) {
        return mutableVersion.export(exportPath);
    }
}
