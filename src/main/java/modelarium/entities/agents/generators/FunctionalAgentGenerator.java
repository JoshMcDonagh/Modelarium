package modelarium.entities.agents.generators;

import modelarium.Config;
import modelarium.entities.agents.Agent;

import java.util.function.Function;

/**
 * An implementation of {@link AgentGenerator} that delegates agent creation logic
 * to a user-defined functional interface.
 *
 * <p>Intended for flexibility and cross-language use (e.g. from Python).</p>
 */
public class FunctionalAgentGenerator extends AgentGenerator {

    private final Function<Config, Agent> generatorFunction;

    /**
     * Constructs a new generator with the specified logic.
     *
     * @param generatorFunction the function used to generate each agent
     */
    public FunctionalAgentGenerator(Function<Config, Agent> generatorFunction) {
        this.generatorFunction = generatorFunction;
    }

    @Override
    protected Agent generateAgent(Config config) {
        return generatorFunction.apply(config);
    }
}
