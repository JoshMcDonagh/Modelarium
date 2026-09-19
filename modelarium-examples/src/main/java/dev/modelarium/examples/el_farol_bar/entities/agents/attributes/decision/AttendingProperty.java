package dev.modelarium.examples.el_farol_bar.entities.agents.attributes.decision;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;

/** Whether this agent has decided to attend El Farol in the current week. */
public final class AttendingProperty extends AgentProperty<Boolean> {
    private boolean attending;

    public AttendingProperty() {
        super("attending", true, AttributeAccessLevel.PUBLIC, Boolean.class);
    }

    @Override
    protected void set(AgentContext context, Boolean attending) {
        this.attending = attending;
    }

    @Override
    protected Boolean get(AgentContext context) {
        return attending;
    }
}
