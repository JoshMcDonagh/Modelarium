package dev.modelarium.examples.sir.entities.agents.attributes.sir;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;

public class SIRStateProperty extends AgentProperty<SIRState> {
    private SIRState state;

    public SIRStateProperty() {
        super("sir_state", true, AttributeAccessLevel.PUBLIC, SIRState.class);
    }

    @Override
    protected void set(AgentContext context, SIRState state) {
        this.state = state;
    }

    @Override
    protected SIRState get(AgentContext context) {
        return state;
    }
}
