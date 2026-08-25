package modelarium.entities.generators;

import modelarium.Config;
import modelarium.entities.Agent;

import java.util.function.BiFunction;
import java.util.random.RandomGenerator;

/**
 * Class for generating agents by delegating creation logic to a user-defined function.
 *
 * <p>This implementation of {@link DefaultAgentGenerator} is intended for flexibility and cross-language use
 * (e.g. from Python).
 */
public class FunctionalDefaultAgentGenerator extends DefaultAgentGenerator {

    /** The function used to generate each agent */
    private final BiFunction<Config, RandomGenerator, Agent> generatorFunction;

    /**
     * Constructs a new generator with the specified logic.
     *
     * @param generatorFunction the function used to generate each agent
     */
    public FunctionalDefaultAgentGenerator(BiFunction<Config, RandomGenerator, Agent> generatorFunction) {
        this.generatorFunction = generatorFunction;
    }

    /**
     * Generates a single agent by applying the user-provided generator function.
     *
     * @param config the model settings passed to the agent during creation
     * @param random the random generator the agent generator can use for constructing agents
     * @return a new {@link Agent} instance
     */
    @Override
    protected Agent generateAgent(Config config, RandomGenerator random) {
        return generatorFunction.apply(config, random);
    }
}
