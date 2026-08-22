package modelarium.entities.environments.generators;

import modelarium.Config;
import modelarium.entities.environments.Environment;

import java.util.function.BiFunction;
import java.util.random.RandomGenerator;

/**
 * Class for generating the environment by delegating creation logic to a user-provided function.
 *
 * <p>This implementation of {@link EnvironmentGenerator} is useful when working across languages (e.g. from
 * Python), or when modular configuration is required without subclassing.
 */
public class FunctionalEnvironmentGenerator extends EnvironmentGenerator {

    /** The function used to generate the environment */
    private final BiFunction<Config, RandomGenerator, Environment> generatorFunction;

    /**
     * Constructs a new functional generator.
     *
     * @param generatorFunction the function used to generate the environment
     */
    public FunctionalEnvironmentGenerator(BiFunction<Config, RandomGenerator, Environment> generatorFunction) {
        this.generatorFunction = generatorFunction;
    }

    /**
     * Generates the environment by applying the user-provided generator function.
     *
     * @param config the global model settings used to configure the environment
     * @param random the random generator the environment generator can use for constructing an environment
     * @return a new {@link Environment} instance
     */
    @Override
    public Environment generateEnvironment(Config config, RandomGenerator random) {
        return generatorFunction.apply(config, random);
    }
}
