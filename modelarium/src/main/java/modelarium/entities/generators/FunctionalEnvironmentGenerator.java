package modelarium.entities.generators;

import modelarium.Config;
import modelarium.entities.Environment;

import java.util.function.BiFunction;
import java.util.random.RandomGenerator;

/**
 * Class for generating the environment by delegating creation logic to a user-provided function.
 *
 * <p>This implementation of {@link EnvironmentGenerator} is useful when working across languages (e.g. from
 * Python), or when modular configuration is required without subclassing.
 */
public class FunctionalEnvironmentGenerator extends EnvironmentGenerator {
    private final BiFunction<Config, RandomGenerator, Environment> generatorFunction;
    private final Runnable resetFunction;

    /**
     * Constructs a new functional generator.
     *
     * @param generatorFunction the function used to generate the environment
     * @param resetFunction the function used to reset the state of the generator the environment is generated
     */
    public FunctionalEnvironmentGenerator(
            BiFunction<Config, RandomGenerator, Environment> generatorFunction,
            Runnable resetFunction
    ) {
        this.generatorFunction = generatorFunction;
        this.resetFunction = resetFunction;
    }

    /**
     * Constructs a new functional generator and uses the default reset logic.
     *
     * @param generatorFunction the function used to generate the environment
     */
    public FunctionalEnvironmentGenerator(BiFunction<Config, RandomGenerator, Environment> generatorFunction) {
        this(generatorFunction, null);
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

    /**
     * Resets the state of the generator.
     */
    @Override
    protected void reset() {
        if (resetFunction == null)
            return;
        resetFunction.run();
    }
}
