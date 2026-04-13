package modelarium.results.immutable;

import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.results.ResultsForEntities;
import modelarium.results.ResultsForEnvironment;

import java.util.Map;

public class ImmutableResultsForEnvironment extends ResultsForEnvironment {
    public ImmutableResultsForEnvironment(ResultsForEnvironment mutableResultsForEnvironment) {
        super(mutableResultsForEnvironment);
    }

    private void throwUnsupportedOperationException() {
        throw new UnsupportedOperationException("ImmutableResultsForEnvironment cannot be modified");
    }

    @Override
    public void mergeWith(
            ResultsForEntities<EnvironmentContext,EnvironmentAttributeSet, AttributeSetLog<EnvironmentContext>> other
    ) {
        throwUnsupportedOperationException();
    }
}
