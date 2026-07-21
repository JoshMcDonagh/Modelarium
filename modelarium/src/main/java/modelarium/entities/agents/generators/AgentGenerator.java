package modelarium.entities.agents.generators;

import modelarium.Config;
import modelarium.entities.agents.AgentSet;

import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Interface for generating the agent population a model will simulate.
 *
 * <p>Implementations are responsible for constructing the model's agents from its configuration settings and for
 * distributing those agents across the model's worker cores.
 */
public interface AgentGenerator {

    /**
     * Generates the complete set of agents the model will contain.
     *
     * @param config the model settings used to construct the agents
     * @param random the random generator the agent generator can use for constructing agents
     * @return a new {@link AgentSet} containing all generated agents
     */
    AgentSet generateAgents(Config config, RandomGenerator random);

    /**
     * Generates the model's agents and distributes them across the model's worker cores.
     *
     * @param config the model settings containing the agent and core counts
     * @param random the random generator the agent generator can use for constructing agents
     * @return a list of {@link AgentSet} objects, one per core
     */
    List<AgentSet> getAgentsForEachCore(Config config, RandomGenerator random);
}
