package modelarium.entities.agents.generators;

import modelarium.Config;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.agents.mutable.AgentSet;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Abstract base class for generating agent populations in a simulation.
 *
 * <p>This class provides methods to:
 * <ul>
 *     <li>Generate a full set of agents based on model settings</li>
 *     <li>Distribute agents evenly across multiple processing cores</li>
 * </ul>
 *
 * <p>Concrete subclasses must implement the {@link #generateAgent(Config, RandomGenerator)} method,
 * which defines how individual agents are constructed.
 */
public abstract class DefaultAgentGenerator implements AgentGenerator {

    /**
     * Generates a complete {@link AgentSet} based on the number of agents specified in the model settings.
     *
     * @param config the simulation configuration containing the agent count
     * @param random the random generator the agent generator can use for constructing agents
     * @return an {@link AgentSet} containing all generated agents
     */
    @Override
    public AgentSet generateAgents(Config config, RandomGenerator random) {
        AgentSet agents = new AgentSet();
        int numOfAgents = config.populationSize();

        for (int i = 0; i < numOfAgents; i++)
            agents.add(generateAgent(config, random));

        return agents;
    }

    /**
     * Distributes agents across processing cores in a round-robin fashion.
     * This ensures an even workload split for multithreaded simulations.
     *
     * @param config the simulation settings containing agent and core counts
     * @param random the random generator the agent generator can use for constructing agents
     * @return a list of {@link AgentSet} objects, one per core
     */
    public List<AgentSet> getAgentsForEachCore(Config config, RandomGenerator random) {
        AgentSet agents = generateAgents(config, random);
        int numOfCores = config.threadCount();

        // If no cores are defined, return an empty list
        if (numOfCores < 1)
            return new ArrayList<>();

        // If only one core is used, assign all agents to it
        if (numOfCores == 1) {
            List<AgentSet> singleCoreList = new ArrayList<>();
            singleCoreList.add(agents);
            return singleCoreList;
        }

        // Prepare empty agent sets for each core
        List<AgentSet> agentsForEachCore = new ArrayList<>();
        for (int i = 0; i < numOfCores; i++)
            agentsForEachCore.add(new AgentSet());

        // Distribute agents evenly across cores (round-robin)
        int core = 0;
        for (Agent agent : agents) {
            agentsForEachCore.get(core).add(agent);
            core = (core + 1) % numOfCores;
        }

        return agentsForEachCore;
    }

    /**
     * Abstract method for generating a single agent instance.
     * Must be implemented by concrete subclasses.
     *
     * @param config the model settings passed to the agent during creation
     * @return a new {@link Agent} instance
     */
    protected abstract Agent generateAgent(Config config, RandomGenerator random);
}
