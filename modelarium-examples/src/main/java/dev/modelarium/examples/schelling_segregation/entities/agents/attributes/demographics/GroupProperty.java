package dev.modelarium.examples.schelling_segregation.entities.agents.attributes.demographics;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;

/** Stores an agent's fixed group membership. */
public final class GroupProperty extends AgentProperty<Group> {
    private Group group;

    public GroupProperty() {
        super("group", true, AttributeAccessLevel.PUBLIC, Group.class);
    }

    @Override
    protected void set(AgentContext context, Group value) {
        group = value;
    }

    @Override
    protected Group get(AgentContext context) {
        return group;
    }
}
