package dev.modelarium.examples.sir.entities.environment;

import dev.modelarium.examples.sir.config.SIRSettings;
import dev.modelarium.examples.sir.config.SettingsLoader;
import dev.modelarium.examples.sir.entities.environment.attributes.geography.HeightProperty;
import dev.modelarium.examples.sir.entities.environment.attributes.geography.WidthProperty;
import dev.modelarium.examples.sir.entities.environment.attributes.prevalence.InfectedPercentageProperty;
import dev.modelarium.examples.sir.entities.environment.attributes.prevalence.NumberOfInfectedProperty;
import dev.modelarium.examples.sir.entities.environment.attributes.prevalence.Prevalence;
import dev.modelarium.examples.sir.entities.environment.attributes.prevalence.PrevalenceUpdateRoutine;
import modelarium.Config;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.sets.mutable.MutableEnvironmentAttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.generators.EnvironmentGenerator;

import java.util.ArrayList;
import java.util.random.RandomGenerator;

public class SIREnvironmentGenerator extends EnvironmentGenerator {
    private final SIRSettings sirSettings;

    public SIREnvironmentGenerator() {
        sirSettings = SettingsLoader.loadSIRConfig("dev/modelarium/examples/sir/sir-config.json");
    }

    @Override
    public Environment generateEnvironment(Config config, RandomGenerator random) {
        ArrayList<MutableEnvironmentAttributeSet> environmentAttributeSets = new ArrayList<MutableEnvironmentAttributeSet>();

        // Geography attributes

        ArrayList<Attribute> environmentGeographyAttributes = new ArrayList<>();

        WidthProperty widthProperty = new WidthProperty();
        widthProperty.set(sirSettings.environment().area().width());
        environmentGeographyAttributes.add(widthProperty);

        HeightProperty heightProperty = new HeightProperty();
        heightProperty.set(sirSettings.environment().area().height());
        environmentGeographyAttributes.add(heightProperty);

        environmentAttributeSets.add(new MutableEnvironmentAttributeSet("geography", environmentGeographyAttributes));

        // Prevalence attributes

        ArrayList<Attribute> environmentPrevalenceAttributes = new ArrayList<>();
        Prevalence prevalence = new Prevalence();

        environmentPrevalenceAttributes.add(new PrevalenceUpdateRoutine(prevalence));
        environmentPrevalenceAttributes.add(new NumberOfInfectedProperty(prevalence));
        environmentPrevalenceAttributes.add(new InfectedPercentageProperty(prevalence));

        environmentAttributeSets.add(new MutableEnvironmentAttributeSet("prevalence", environmentPrevalenceAttributes));

        // Environment generation

        return new Environment(environmentAttributeSets);
    }
}
