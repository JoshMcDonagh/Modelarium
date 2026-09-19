package dev.modelarium.examples.axelrod_cultural_dissemination.entities.environment.attributes.metrics;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.contexts.EnvironmentContext;

/** Recalculates aggregate cultural metrics from the population snapshot visible to the environment. */
public final class CulturalMetricsUpdateRoutine extends EnvironmentRoutine {
    private final CulturalMetrics metrics;
    private final int width;
    private final int height;

    public CulturalMetricsUpdateRoutine(
            CulturalMetrics metrics,
            int width,
            int height
    ) {
        super("update_cultural_metrics", AttributeAccessLevel.PRIVATE);
        this.metrics = metrics;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void run(EnvironmentContext context) {
        metrics.update(context, width, height);
    }
}
