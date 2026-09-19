package dev.modelarium.examples.axelrod_cultural_dissemination.entities.environment.attributes.metrics;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.EnvironmentContext;

/** Mean cultural similarity across undirected cardinal-neighbour pairs. */
public final class MeanNeighbourSimilarityProperty extends EnvironmentProperty<Double> {
    private final CulturalMetrics metrics;

    public MeanNeighbourSimilarityProperty(CulturalMetrics metrics) {
        super("mean_neighbour_similarity", true, AttributeAccessLevel.PRIVATE, Double.class);
        this.metrics = metrics;
    }

    @Override
    protected void set(EnvironmentContext context, Double value) {
        throw new UnsupportedOperationException("Mean neighbour similarity is calculated from the population");
    }

    @Override
    protected Double get(EnvironmentContext context) {
        return metrics.meanNeighbourSimilarity();
    }
}
