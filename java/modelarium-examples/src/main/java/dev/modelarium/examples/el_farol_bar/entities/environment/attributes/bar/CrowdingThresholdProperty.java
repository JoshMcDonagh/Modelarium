package dev.modelarium.examples.el_farol_bar.entities.environment.attributes.bar;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.EnvironmentContext;

/** The greatest attendance level an agent regards as comfortably uncrowded. */
public final class CrowdingThresholdProperty extends EnvironmentProperty<Integer> {
    private int crowdingThreshold;

    public CrowdingThresholdProperty() {
        super("crowding_threshold", false, AttributeAccessLevel.PUBLIC, Integer.class);
    }

    @Override
    protected void set(EnvironmentContext context, Integer crowdingThreshold) {
        this.crowdingThreshold = crowdingThreshold;
    }

    @Override
    protected Integer get(EnvironmentContext context) {
        return crowdingThreshold;
    }
}
