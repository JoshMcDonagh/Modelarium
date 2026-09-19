package dev.modelarium.examples.axelrod_cultural_dissemination.entities.environment.attributes.metrics;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.EnvironmentContext;

/** Number of sites in the largest contiguous identical-culture region. */
public final class LargestCulturalRegionSizeProperty extends EnvironmentProperty<Integer> {
    private final CulturalMetrics metrics;

    public LargestCulturalRegionSizeProperty(CulturalMetrics metrics) {
        super("largest_cultural_region_size", true, AttributeAccessLevel.PRIVATE, Integer.class);
        this.metrics = metrics;
    }

    @Override
    protected void set(EnvironmentContext context, Integer value) {
        throw new UnsupportedOperationException("Largest cultural region size is calculated from the population");
    }

    @Override
    protected Integer get(EnvironmentContext context) {
        return metrics.largestRegionSize();
    }
}
