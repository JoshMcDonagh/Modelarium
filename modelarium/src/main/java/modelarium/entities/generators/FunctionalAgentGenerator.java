package modelarium.entities.generators;

import modelarium.Config;
import modelarium.entities.agentsets.AgentSet;

import java.util.List;
import java.util.function.BiFunction;
import java.util.random.RandomGenerator;

/**
 * Class for generating agents by delegating creation logic to a user-defined function.
 *
 * <p>This implementation of {@link AgentGenerator} is intended for flexibility and cross-language use
 * (e.g. from Python).
 */
public class FunctionalAgentGenerator extends AgentGenerator {
    private final BiFunction<Config, RandomGenerator, List<AgentSet>> getAgentsForEachThreadFunction;
    private final Runnable resetFunction;

    /**
     * Constructs a new generator with the specified logic.
     *
     * @param getAgentsForEachThreadFunction the function used to generate agent sets for each thread
     * @param resetFunction the function used to reset the state of the generator after agents are generated
     */
    public FunctionalAgentGenerator(
            BiFunction<Config, RandomGenerator, List<AgentSet>> getAgentsForEachThreadFunction,
            Runnable resetFunction
    ) {
        this.getAgentsForEachThreadFunction = getAgentsForEachThreadFunction;
        this.resetFunction = resetFunction;
    }

    /**
     * Constructs a new generator with the specified logic and uses the default reset logic.
     *
     * @param getAgentsForEachThreadFunction the function used to generate agent sets for each thread
     */
    public FunctionalAgentGenerator(BiFunction<Config, RandomGenerator, List<AgentSet>> getAgentsForEachThreadFunction) {
        this(getAgentsForEachThreadFunction, null);
    }

    /**
     * Generates the model's agents using the logic specified during construction and distributes them across the
     * model's worker cores.
     *
     * @param config the model settings containing the agent and thread counts
     * @param random the random generator the agent generator can use for constructing agents
     * @return a list of {@link AgentSet} objects, one per thread
     */
    @Override
    public List<AgentSet> generateAgentsForEachThread(Config config, RandomGenerator random) {
        return getAgentsForEachThreadFunction.apply(config, random);
    }

    /**
     * Resets the state of the generator.
     */
    @Override
    public void reset() {
        if (resetFunction == null)
            return;
        resetFunction.run();
    }
}
