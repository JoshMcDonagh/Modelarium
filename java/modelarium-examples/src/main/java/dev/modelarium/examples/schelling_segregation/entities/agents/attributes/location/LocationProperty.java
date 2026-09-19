package dev.modelarium.examples.schelling_segregation.entities.agents.attributes.location;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;

/** Stores and logs an agent's current grid cell. */
public final class LocationProperty extends AgentProperty<Cell> {
    private Cell cell;

    public LocationProperty() {
        super("location", true, AttributeAccessLevel.PUBLIC, Cell.class);
    }

    @Override
    protected void set(AgentContext context, Cell value) {
        cell = value;
    }

    @Override
    protected Cell get(AgentContext context) {
        return cell;
    }
}
