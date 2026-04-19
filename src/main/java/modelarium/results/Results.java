package modelarium.results;

import modelarium.results.immutable.ImmutableResults;
import modelarium.results.mutable.MutableResults;

public sealed interface Results permits MutableResults, ImmutableResults {
    ResultsForAgents agents();
    ResultsForEnvironment environment();
}
