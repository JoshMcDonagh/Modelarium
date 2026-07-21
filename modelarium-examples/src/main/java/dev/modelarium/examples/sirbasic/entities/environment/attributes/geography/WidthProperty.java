package dev.modelarium.examples.sirbasic.entities.environment.attributes.geography;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.EnvironmentContext;

public class WidthProperty extends EnvironmentProperty<Integer> {
    private Integer width = null;

    public WidthProperty() {
        super("width", false, AttributeAccessLevel.PUBLIC, Integer.class);
    }

    @Override
    protected void set(EnvironmentContext context, Integer width) {
        if (this.width != null)
            return;
        this.width = width;
    }

    @Override
    protected Integer get(EnvironmentContext context) {
        return width;
    }
}
