package modelarium.results;


import modelarium.results.immutable.ImmutableResults;
import modelarium.results.mutable.MutableResults;

import java.nio.file.Path;

/**
 * Interface for providing access to the results of a model run.
 *
 * <p>This interface exposes the run's agent-level and environment-level results, and is implemented by
 * {@link MutableResults}, which the model builds up during a run, and {@link ImmutableResults}, which is handed to
 * the user afterwards.
 */
public sealed interface Results permits MutableResults, ImmutableResults {

    /**
     * Returns the agent-level results of the model run.
     *
     * @return the run's {@link ResultsForAgents} instance
     */
    ResultsForAgents agents();

    /**
     * Returns the environment-level results of the model run.
     *
     * @return the run's {@link ResultsForEnvironment} instance
     */
    ResultsForEnvironment environment();

    /**
     * Exports the results of the model to a given export directory given as a {@link String}
     *
     * @param exportDir the directory to export the results to
     * @return the {@link Path} directory containing the exported results
     */
    Path export(String exportDir);

    /**
     * Exports the results of the model to a given export directory given as a {@link Path}
     *
     * @param exportPath the path directory to export the results to
     * @return the {@link Path} directory containing the exported results
     */
    Path export(Path exportPath);
}
