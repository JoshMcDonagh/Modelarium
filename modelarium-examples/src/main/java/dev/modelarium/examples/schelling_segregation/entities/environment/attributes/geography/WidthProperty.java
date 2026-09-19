package dev.modelarium.examples.schelling_segregation.entities.environment.attributes.geography;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.EnvironmentContext;

/** Stores the grid width. */
public final class WidthProperty extends EnvironmentProperty<Integer> {
    private Integer width;

    public WidthProperty() {
        super("width", true, AttributeAccessLevel.PUBLIC, Integer.class);
    }

    @Override
    protected void set(EnvironmentContext context, Integer value) {
        width = value;
    }

    @Override
    protected Integer get(EnvironmentContext context) {
        return width;
    }
}
