package modelarium.agents;

import modelarium.ModelConfig;

import java.util.function.Function;

/**
 * An implementation of {@link AgentGenerator} that delegates agent creation logic
 * to a user-defined functional interface.
 *
 * <p>Intended for flexibility and cross-language use (e.g. from Python).</p>
 */
public class FunctionalAgentGenerator extends AgentGenerator {

    private final Function<ModelConfig, Agent> generatorFunction;

    /**
     * Constructs a new generator with the specified logic.
     *
     * @param generatorFunction the function used to generate each agent
     */
    public FunctionalAgentGenerator(Function<ModelConfig, Agent> generatorFunction) {
        this.generatorFunction = generatorFunction;
    }

    @Override
    protected Agent generateAgent(ModelConfig modelConfig) {
        return generatorFunction.apply(modelConfig);
    }
}
