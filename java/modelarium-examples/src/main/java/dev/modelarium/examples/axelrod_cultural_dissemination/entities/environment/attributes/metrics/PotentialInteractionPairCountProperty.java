package dev.modelarium.examples.axelrod_cultural_dissemination.entities.environment.attributes.metrics;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.EnvironmentContext;

/** Number of neighbouring pairs whose similarity lies strictly between zero and one. */
public final class PotentialInteractionPairCountProperty extends EnvironmentProperty<Integer> {
    private final CulturalMetrics metrics;

    public PotentialInteractionPairCountProperty(CulturalMetrics metrics) {
        super("potential_interaction_pair_count", true, AttributeAccessLevel.PRIVATE, Integer.class);
        this.metrics = metrics;
    }

    @Override
    protected void set(EnvironmentContext context, Integer value) {
        throw new UnsupportedOperationException("Potential interaction count is calculated from the population");
    }

    @Override
    protected Integer get(EnvironmentContext context) {
        return metrics.potentialInteractionPairCount();
    }
}
