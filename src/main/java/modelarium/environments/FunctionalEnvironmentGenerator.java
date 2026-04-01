package modelarium.environments;

import modelarium.ModelConfig;

import java.util.function.Function;

/**
 * A functional implementation of {@link EnvironmentGenerator} that delegates
 * environment creation to a user-provided function.
 *
 * <p>Useful when working across languages (e.g. from Python), or when
 * modular configuration is required without subclassing.</p>
 */
public class FunctionalEnvironmentGenerator extends EnvironmentGenerator {

    private final Function<ModelConfig, Environment> generatorFunction;

    /**
     * Constructs a new functional generator.
     *
     * @param generatorFunction the function used to generate the environment
     */
    public FunctionalEnvironmentGenerator(Function<ModelConfig, Environment> generatorFunction) {
        this.generatorFunction = generatorFunction;
    }

    @Override
    public Environment generateEnvironment(ModelConfig modelConfig) {
        return generatorFunction.apply(modelConfig);
    }
}
