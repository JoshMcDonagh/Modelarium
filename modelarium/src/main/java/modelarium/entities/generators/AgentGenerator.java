package modelarium.entities.generators;

import modelarium.Config;
import modelarium.entities.agentsets.AgentSet;
import modelarium.internal.Internal;

import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Abstract class for generating the agent population a model will simulate.
 *
 * <p>Implementations are responsible for constructing the model's agents from its configuration settings and for
 * distributing those agents across the model's worker threads.
 */
public abstract class AgentGenerator {
    /**
     * Generates the model's agents and distributes them across the model's worker cores.
     *
     * @param config the model settings containing the agent and thread counts
     * @param random the random generator the agent generator can use for constructing agents
     * @return a list of {@link AgentSet} objects, one per thread
     */
    public abstract List<AgentSet> generateAgentsForEachThread(Config config, RandomGenerator random);

    /**
     * Internal method for resetting the state of the generator.
     *
     * @hidden
     */
    @Internal
    public void internalReset() {
        reset();
    }

    /**
     * Resets any temporary state retained while generating a population.
     *
     * <p>The model calls this hook after every generation attempt, including an attempt which throws. Stateful
     * generators should override it so the same generator can be reused for a later run.
     */
    protected void reset() {
        // No-op by default
    }
}
