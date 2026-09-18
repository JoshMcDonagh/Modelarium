package modelarium.entities.generators;

import modelarium.Config;
import modelarium.entities.Agent;
import modelarium.entities.agentsets.AgentSet;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Abstract base class for generating agent populations in a simulation.
 *
 * <p>This class provides methods to:
 * <ul>
 *     <li>Generate a full set of agents based on model settings</li>
 *     <li>Distribute agents evenly across multiple processing threads</li>
 * </ul>
 *
 * <p>Concrete subclasses must implement the {@link #generateAgent(Config, RandomGenerator)} method,
 * which defines how individual agents are constructed.
 */
public abstract class DefaultAgentGenerator extends AgentGenerator {

    /**
     * Constructs a default agent generator.
     */
    public DefaultAgentGenerator() {}

    /**
     * Generates a complete {@link AgentSet} based on the number of agents specified in the model settings.
     *
     * @param config the simulation configuration containing the agent count
     * @param random the random generator the agent generator can use for constructing agents
     * @return an {@link AgentSet} containing all generated agents
     */
    public AgentSet generateAgents(Config config, RandomGenerator random) {
        AgentSet agents = new AgentSet();
        int numOfAgents = config.populationSize();

        for (int i = 0; i < numOfAgents; i++)
            agents.add(generateAgent(config, random));

        return agents;
    }

    /**
     * Distributes agents across processing threads in a round-robin fashion.
     * This ensures an even workload split for multithreaded simulations.
     *
     * @param config the simulation settings containing agent and thread counts
     * @param random the random generator the agent generator can use for constructing agents
     * @return a list of {@link AgentSet} objects, one per thread
     */
    public List<AgentSet> generateAgentsForEachThread(Config config, RandomGenerator random) {
        AgentSet agents = generateAgents(config, random);
        int numOfThreads = config.threadCount();

        // If no threads are defined, return an empty list
        if (numOfThreads < 1)
            return new ArrayList<>();

        // If only one thread is used, assign all agents to it
        if (numOfThreads == 1) {
            List<AgentSet> singleThreadList = new ArrayList<>();
            singleThreadList.add(agents);
            return singleThreadList;
        }

        // Prepare empty agent sets for each thread
        List<AgentSet> agentsForEachThread = new ArrayList<>();
        for (int i = 0; i < numOfThreads; i++)
            agentsForEachThread.add(new AgentSet());

        // Distribute agents evenly across threads (round-robin)
        int thread = 0;
        for (Agent agent : agents) {
            agentsForEachThread.get(thread).add(agent);
            thread = (thread + 1) % numOfThreads;
        }

        return agentsForEachThread;
    }

    /**
     * Abstract method for generating a single agent instance.
     * Must be implemented by concrete subclasses.
     *
     * @param config the model settings passed to the agent during creation
     * @param random the random generator the agent generator can use while constructing the agent
     * @return a new {@link Agent} instance
     */
    protected abstract Agent generateAgent(Config config, RandomGenerator random);
}
