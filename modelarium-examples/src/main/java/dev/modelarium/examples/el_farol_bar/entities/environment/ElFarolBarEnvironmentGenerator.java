package dev.modelarium.examples.el_farol_bar.entities.environment;

import dev.modelarium.examples.el_farol_bar.config.ElFarolBarSettings;
import dev.modelarium.examples.el_farol_bar.entities.environment.attributes.bar.AttendanceProperty;
import dev.modelarium.examples.el_farol_bar.entities.environment.attributes.bar.CrowdingThresholdProperty;
import modelarium.Config;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.sets.mutable.EnvironmentAttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.generators.EnvironmentGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/** Generates the bar environment shared by the agents. */
public final class ElFarolBarEnvironmentGenerator extends EnvironmentGenerator {
    private final ElFarolBarSettings settings;

    public ElFarolBarEnvironmentGenerator(ElFarolBarSettings settings) {
        this.settings = settings;
    }

    @Override
    public Environment generateEnvironment(Config config, RandomGenerator random) {
        CrowdingThresholdProperty crowdingThresholdProperty = new CrowdingThresholdProperty();
        crowdingThresholdProperty.set(settings.bar().crowdingThreshold());

        List<Attribute> barAttributes = new ArrayList<>();
        barAttributes.add(crowdingThresholdProperty);
        barAttributes.add(new AttendanceProperty());

        return new Environment(List.of(
                new EnvironmentAttributeSet("bar", barAttributes)
        ));
    }
}
