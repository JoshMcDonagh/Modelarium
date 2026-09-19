package dev.modelarium.examples.el_farol_bar.entities.agents.attributes.decision;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;

/** The attendance forecast made by the agent's currently active predictor. */
public final class PredictedAttendanceProperty extends AgentProperty<Integer> {
    private int predictedAttendance;

    public PredictedAttendanceProperty() {
        super("predicted_attendance", true, AttributeAccessLevel.PUBLIC, Integer.class);
    }

    @Override
    protected void set(AgentContext context, Integer predictedAttendance) {
        this.predictedAttendance = predictedAttendance;
    }

    @Override
    protected Integer get(AgentContext context) {
        return predictedAttendance;
    }
}
