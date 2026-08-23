package dev.modelarium.examples.el_farol_bar.entities.agents.attributes.decision;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;

/** The name of the predictor the agent currently considers most accurate. */
public final class ActivePredictorProperty extends AgentProperty<String> {
    private String activePredictor = "";

    public ActivePredictorProperty() {
        super("active_predictor", true, AttributeAccessLevel.PUBLIC, String.class);
    }

    @Override
    protected void set(AgentContext context, String activePredictor) {
        this.activePredictor = activePredictor;
    }

    @Override
    protected String get(AgentContext context) {
        return activePredictor;
    }
}
