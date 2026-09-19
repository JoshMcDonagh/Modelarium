package dev.modelarium.examples.axelrod_cultural_dissemination.entities.environment.attributes.metrics;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.EnvironmentContext;

/** Number of contiguous cardinal-neighbour regions whose sites have identical cultures. */
public final class CulturalRegionCountProperty extends EnvironmentProperty<Integer> {
    private final CulturalMetrics metrics;

    public CulturalRegionCountProperty(CulturalMetrics metrics) {
        super("cultural_region_count", true, AttributeAccessLevel.PRIVATE, Integer.class);
        this.metrics = metrics;
    }

    @Override
    protected void set(EnvironmentContext context, Integer value) {
        throw new UnsupportedOperationException("Cultural region count is calculated from the population");
    }

    @Override
    protected Integer get(EnvironmentContext context) {
        return metrics.regionCount();
    }
}
