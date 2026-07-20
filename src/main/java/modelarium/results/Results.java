package modelarium.results;


import modelarium.results.immutable.ImmutableResults;
import modelarium.results.mutable.MutableResults;

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
}
