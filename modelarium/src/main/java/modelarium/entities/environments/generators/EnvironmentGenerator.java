package modelarium.entities.environments.generators;

import modelarium.Config;
import modelarium.entities.environments.MutableEnvironment;

import java.util.random.RandomGenerator;

/**
 * Abstract base class responsible for generating the simulation {@link MutableEnvironment}.
 *
 * <p>Concrete subclasses should use the {@link Config} to construct and return
 * a new instance of {@link MutableEnvironment}, complete with its attribute sets and configuration.
 *
 * <p>This abstraction allows environments to be modular and varied across different simulations.
 */
public abstract class EnvironmentGenerator {

    /**
     * Creates and returns a fully initialised {@link MutableEnvironment} for the simulation.
     *
     * @param config the global model settings used to configure the environment
     * @param random the random generator the environment generator can use for constructing an environment
     * @return a new {@link MutableEnvironment} instance
     */
    public abstract MutableEnvironment generateEnvironment(Config config, RandomGenerator random);
}
