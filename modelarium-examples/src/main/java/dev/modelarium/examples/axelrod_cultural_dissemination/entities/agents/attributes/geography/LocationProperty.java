package dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.attributes.geography;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;

/** Stores an agent's fixed site on the cultural territory. */
public final class LocationProperty extends AgentProperty<GridPosition> {
    private GridPosition location;

    public LocationProperty() {
        super("location", false, AttributeAccessLevel.PUBLIC, GridPosition.class);
    }

    @Override
    protected void set(AgentContext context, GridPosition value) {
        location = value;
    }

    @Override
    protected GridPosition get(AgentContext context) {
        return location;
    }
}
