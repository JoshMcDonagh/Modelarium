package dev.modelarium.examples.el_farol_bar.entities.environment.attributes.bar;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.EnvironmentContext;

/** The number of agents in attendance at the bar. */
public final class AttendanceProperty extends EnvironmentProperty<Integer> {
    private int attendance;

    public AttendanceProperty() {
        super("attendance", true, AttributeAccessLevel.PRIVATE, Integer.class);
    }

    @Override
    protected void set(EnvironmentContext context, Integer value) {
        throw new UnsupportedOperationException("Attendance property should not be set externally");
    }

    @Override
    protected Integer get(EnvironmentContext context) {
        return attendance;
    }

    @Override
    protected void run(EnvironmentContext context) {
        attendance = context.getFilteredAgents(
                agent -> (boolean) agent.getProperty("decision", "attending").get()
        ).size();
    }
}
