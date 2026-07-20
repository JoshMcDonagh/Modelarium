package modelarium.entities.agents.generators;

import modelarium.Config;
import modelarium.entities.agents.Agent;

import java.util.function.Function;

/**
 * Class for generating agents by delegating creation logic to a user-defined function.
 *
 * <p>This implementation of {@link DefaultAgentGenerator} is intended for flexibility and cross-language use
 * (e.g. from Python).
 */
public class FunctionalDefaultAgentGenerator extends DefaultAgentGenerator {

    /** The function used to generate each agent */
    private final Function<Config, Agent> generatorFunction;

    /**
     * Constructs a new generator with the specified logic.
     *
     * @param generatorFunction the function used to generate each agent
     */
    public FunctionalDefaultAgentGenerator(Function<Config, Agent> generatorFunction) {
        this.generatorFunction = generatorFunction;
    }

    /**
     * Generates a single agent by applying the user-provided generator function.
     *
     * @param config the model settings passed to the agent during creation
     * @return a new {@link Agent} instance
     */
    @Override
    protected Agent generateAgent(Config config) {
        return generatorFunction.apply(config);
    }
}
