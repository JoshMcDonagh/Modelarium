package dev.modelarium.examples.schelling_segregation.entities.environment;

import dev.modelarium.examples.schelling_segregation.config.SchellingSegregationSettings;
import dev.modelarium.examples.schelling_segregation.entities.environment.attributes.geography.HeightProperty;
import dev.modelarium.examples.schelling_segregation.entities.environment.attributes.geography.WidthProperty;
import dev.modelarium.examples.schelling_segregation.entities.environment.attributes.segregation.SimilarityThresholdProperty;
import modelarium.Config;
import modelarium.entities.Environment;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.sets.EnvironmentAttributeSet;
import modelarium.entities.generators.EnvironmentGenerator;

import java.util.List;
import java.util.random.RandomGenerator;

/** Generates the residential-grid environment for the Schelling model. */
public final class SchellingEnvironmentGenerator extends EnvironmentGenerator {
    private final SchellingSegregationSettings settings;

    public SchellingEnvironmentGenerator(SchellingSegregationSettings settings) {
        this.settings = settings;
    }

    @Override
    public Environment generateEnvironment(Config config, RandomGenerator random) {
        WidthProperty widthProperty = new WidthProperty();
        widthProperty.set(settings.grid().width());

        HeightProperty heightProperty = new HeightProperty();
        heightProperty.set(settings.grid().height());

        SimilarityThresholdProperty thresholdProperty = new SimilarityThresholdProperty();
        thresholdProperty.set(settings.segregation().minimumSimilarNeighbourFraction());

        List<Attribute> geographyAttributes = List.of(widthProperty, heightProperty);
        List<Attribute> segregationAttributes = List.of(thresholdProperty);

        return new Environment(List.of(
                new EnvironmentAttributeSet("geography", geographyAttributes),
                new EnvironmentAttributeSet("segregation", segregationAttributes)
        ));
    }
}
