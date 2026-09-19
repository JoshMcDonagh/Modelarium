package dev.modelarium.examples.schelling_segregation.entities.environment.attributes.geography;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.EnvironmentContext;

/** Stores the grid height. */
public final class HeightProperty extends EnvironmentProperty<Integer> {
    private Integer height;

    public HeightProperty() {
        super("height", true, AttributeAccessLevel.PUBLIC, Integer.class);
    }

    @Override
    protected void set(EnvironmentContext context, Integer value) {
        height = value;
    }

    @Override
    protected Integer get(EnvironmentContext context) {
        return height;
    }
}
