package dev.modelarium.examples.axelrod_cultural_dissemination.entities.environment.attributes.metrics;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.contexts.EnvironmentContext;

/** Recalculates aggregate cultural metrics at a configurable event interval. */
public final class CulturalMetricsUpdateRoutine extends EnvironmentRoutine {
    private final CulturalMetrics metrics;
    private final int width;
    private final int height;
    private final int measurementIntervalEvents;
    private final int eventsPerModelariumTick;

    public CulturalMetricsUpdateRoutine(
            CulturalMetrics metrics,
            int width,
            int height,
            int measurementIntervalEvents,
            int eventsPerModelariumTick
    ) {
        super("update_cultural_metrics", AttributeAccessLevel.PRIVATE);
        this.metrics = metrics;
        this.width = width;
        this.height = height;
        this.measurementIntervalEvents = measurementIntervalEvents;
        this.eventsPerModelariumTick = eventsPerModelariumTick;
    }

    @Override
    protected void run(EnvironmentContext context) {
        // The environment observes the population snapshot from the beginning of the worker tick. By the time the
        // coordinator runs the environment the shared clock has already advanced, so currentTick - 1 is the number
        // of Axelrod activation events represented by the environment's currently visible agent state.
        long observedEvents = (context.getClock().currentTick() - 1L) * eventsPerModelariumTick;

        if (!metrics.initialized() || observedEvents % measurementIntervalEvents == 0)
            metrics.update(context, width, height);
    }
}
