package dev.modelarium.examples.sir.entities.environment;

import dev.modelarium.examples.sir.config.SIRSettings;
import dev.modelarium.examples.sir.entities.environment.attributes.geography.HeightProperty;
import dev.modelarium.examples.sir.entities.environment.attributes.geography.WidthProperty;
import dev.modelarium.examples.sir.entities.environment.attributes.prevalence.InfectedPercentageProperty;
import dev.modelarium.examples.sir.entities.environment.attributes.prevalence.NumberOfInfectedProperty;
import dev.modelarium.examples.sir.entities.environment.attributes.prevalence.Prevalence;
import dev.modelarium.examples.sir.entities.environment.attributes.prevalence.PrevalenceUpdateRoutine;
import modelarium.Config;
import modelarium.entities.Environment;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.sets.EnvironmentAttributeSet;
import modelarium.entities.generators.EnvironmentGenerator;

import java.util.ArrayList;
import java.util.Objects;
import java.util.random.RandomGenerator;

public class SIREnvironmentGenerator extends EnvironmentGenerator {
    private final SIRSettings sirSettings;

    public SIREnvironmentGenerator(SIRSettings sirSettings) {
        this.sirSettings = Objects.requireNonNull(sirSettings, "sirSettings must be set");
    }

    @Override
    public Environment generateEnvironment(Config config, RandomGenerator random) {
        ArrayList<EnvironmentAttributeSet> environmentAttributeSets = new ArrayList<EnvironmentAttributeSet>();

        // Geography attributes

        ArrayList<Attribute> environmentGeographyAttributes = new ArrayList<>();

        WidthProperty widthProperty = new WidthProperty();
        widthProperty.set(sirSettings.environment().area().width());
        environmentGeographyAttributes.add(widthProperty);

        HeightProperty heightProperty = new HeightProperty();
        heightProperty.set(sirSettings.environment().area().height());
        environmentGeographyAttributes.add(heightProperty);

        environmentAttributeSets.add(new EnvironmentAttributeSet("geography", environmentGeographyAttributes));

        // Prevalence attributes

        ArrayList<Attribute> environmentPrevalenceAttributes = new ArrayList<>();
        Prevalence prevalence = new Prevalence();

        environmentPrevalenceAttributes.add(new PrevalenceUpdateRoutine(prevalence));
        environmentPrevalenceAttributes.add(new NumberOfInfectedProperty(prevalence));
        environmentPrevalenceAttributes.add(new InfectedPercentageProperty(prevalence));

        environmentAttributeSets.add(new EnvironmentAttributeSet("prevalence", environmentPrevalenceAttributes));

        // Environment generation

        return new Environment(environmentAttributeSets);
    }
}
