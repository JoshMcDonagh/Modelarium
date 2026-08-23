package dev.modelarium.examples.sir.entities.environment.attributes.geography;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.EnvironmentContext;

public class HeightProperty extends EnvironmentProperty<Integer> {
    private Integer height = null;

    public HeightProperty() {
        super("height", false, AttributeAccessLevel.PUBLIC, Integer.class);
    }

    @Override
    protected void set(EnvironmentContext context, Integer height) {
        if (this.height != null)
            return;
        this.height = height;
    }

    @Override
    protected Integer get(EnvironmentContext context) {
        return height;
    }
}
