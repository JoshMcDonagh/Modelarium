package modelarium.results.immutable;

import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.results.ResultsForAgents;
import modelarium.results.ResultsForEntities;

public class ImmutableResultsForAgents extends ResultsForAgents {
    public ImmutableResultsForAgents(ResultsForAgents mutableResultsForAgents) {
        super(mutableResultsForAgents);
    }

    private void throwUnsupportedOperationException() {
        throw new UnsupportedOperationException("ImmutableResultsForAgents cannot be modified");
    }

    @Override
    public void mergeWith(
            ResultsForEntities<AgentContext, AgentAttributeSet, AttributeSetLog<AgentContext>> other
    ) {
        throwUnsupportedOperationException();
    }
}
