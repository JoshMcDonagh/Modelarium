package dev.modelarium.examples.el_farol_bar.entities.agents.attributes.decision;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;

/** The latest public attendance figure known to this agent when it makes its current decision. */
public final class LastObservedAttendanceProperty extends AgentProperty<Integer> {
    private int lastObservedAttendance;

    public LastObservedAttendanceProperty() {
        super("last_observed_attendance", true, AttributeAccessLevel.PUBLIC, Integer.class);
    }

    @Override
    protected void set(AgentContext context, Integer lastObservedAttendance) {
        this.lastObservedAttendance = lastObservedAttendance;
    }

    @Override
    protected Integer get(AgentContext context) {
        return lastObservedAttendance;
    }
}
