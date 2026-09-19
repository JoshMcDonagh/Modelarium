package dev.modelarium.examples.schelling_segregation.entities.environment.attributes.segregation;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.EnvironmentContext;

/** Stores the minimum fraction of similar occupied neighbours required for satisfaction. */
public final class SimilarityThresholdProperty extends EnvironmentProperty<Double> {
    private Double threshold;

    public SimilarityThresholdProperty() {
        super("minimum_similar_neighbour_fraction", true, AttributeAccessLevel.PUBLIC, Double.class);
    }

    @Override
    protected void set(EnvironmentContext context, Double value) {
        threshold = value;
    }

    @Override
    protected Double get(EnvironmentContext context) {
        return threshold;
    }
}
