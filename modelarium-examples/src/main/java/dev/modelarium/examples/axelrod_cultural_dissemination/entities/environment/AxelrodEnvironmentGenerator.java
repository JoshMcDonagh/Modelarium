package dev.modelarium.examples.axelrod_cultural_dissemination.entities.environment;

import dev.modelarium.examples.axelrod_cultural_dissemination.config.AxelrodCulturalDisseminationSettings;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.environment.attributes.metrics.CulturalMetrics;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.environment.attributes.metrics.CulturalMetricsUpdateRoutine;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.environment.attributes.metrics.CulturalRegionCountProperty;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.environment.attributes.metrics.LargestCulturalRegionSizeProperty;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.environment.attributes.metrics.MeanNeighbourSimilarityProperty;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.environment.attributes.metrics.PotentialInteractionPairCountProperty;
import modelarium.Config;
import modelarium.entities.Environment;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.sets.EnvironmentAttributeSet;
import modelarium.entities.generators.EnvironmentGenerator;

import java.util.List;
import java.util.random.RandomGenerator;

/** Generates the environment used to measure aggregate Axelrod cultural dynamics. */
public final class AxelrodEnvironmentGenerator extends EnvironmentGenerator {
    private final AxelrodCulturalDisseminationSettings settings;

    public AxelrodEnvironmentGenerator(AxelrodCulturalDisseminationSettings settings) {
        this.settings = settings;
    }

    @Override
    public Environment generateEnvironment(Config config, RandomGenerator random) {
        CulturalMetrics metrics = new CulturalMetrics();

        List<Attribute> metricAttributes = List.of(
                new CulturalMetricsUpdateRoutine(
                        metrics,
                        settings.grid().width(),
                        settings.grid().height(),
                        settings.modelSettings().metricMeasurementIntervalEvents(),
                        settings.modelSettings().eventsPerModelariumTick()
                ),
                new CulturalRegionCountProperty(metrics),
                new LargestCulturalRegionSizeProperty(metrics),
                new PotentialInteractionPairCountProperty(metrics),
                new MeanNeighbourSimilarityProperty(metrics)
        );

        return new Environment(List.of(
                new EnvironmentAttributeSet("cultural_metrics", metricAttributes)
        ));
    }
}
