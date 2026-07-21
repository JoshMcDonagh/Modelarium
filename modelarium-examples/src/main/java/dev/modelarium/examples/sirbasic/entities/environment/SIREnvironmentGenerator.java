package dev.modelarium.examples.sirbasic.entities.environment;

import dev.modelarium.examples.sirbasic.config.SettingsLoader;
import dev.modelarium.examples.sirbasic.config.SIRSettings;
import dev.modelarium.examples.sirbasic.entities.environment.attributes.geography.HeightProperty;
import dev.modelarium.examples.sirbasic.entities.environment.attributes.geography.WidthProperty;
import modelarium.Config;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.EnvironmentGenerator;

import java.util.ArrayList;
import java.util.random.RandomGenerator;

public class SIREnvironmentGenerator extends EnvironmentGenerator {
    private final SIRSettings sirSettings;

    public SIREnvironmentGenerator() {
        sirSettings = SettingsLoader.loadSIRConfig("sir-config.json");
    }

    @Override
    public Environment generateEnvironment(Config config, RandomGenerator random) {
        ArrayList<EnvironmentAttributeSet> environmentAttributeSets = new ArrayList<EnvironmentAttributeSet>();
        ArrayList<Attribute> environmentGeographyAttributes = new ArrayList<>();

        WidthProperty widthProperty = new WidthProperty();
        widthProperty.set(sirSettings.environment().area().width());
        environmentGeographyAttributes.add(widthProperty);

        HeightProperty heightProperty = new HeightProperty();
        heightProperty.set(sirSettings.environment().area().height());
        environmentGeographyAttributes.add(heightProperty);

        environmentAttributeSets.add(new EnvironmentAttributeSet("geography", environmentGeographyAttributes));

        return new Environment(environmentAttributeSets);
    }
}
