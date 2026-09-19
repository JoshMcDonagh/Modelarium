package dev.modelarium.examples.epstein_axtell_sugarscape.entities.environment;

import modelarium.Config;
import modelarium.entities.Environment;
import modelarium.entities.generators.EnvironmentGenerator;

import java.util.List;
import java.util.random.RandomGenerator;

/** Sugarscape's landscape state is maintained by the experiment scheduler; the Modelarium environment is empty. */
public final class SugarscapeEnvironmentGenerator extends EnvironmentGenerator {
    @Override
    public Environment generateEnvironment(Config config, RandomGenerator random) {
        return new Environment(List.of());
    }
}
