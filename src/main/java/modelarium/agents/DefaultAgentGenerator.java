package modelarium.agents;

import modelarium.ModelSettings;

/**
 * A basic implementation of {@link AgentGenerator} that creates agents with
 * deep-copied base attribute sets and assigns them unique sequential names.
 *
 * <p>The agent names follow the format {@code Agent_0}, {@code Agent_1}, and so on.
 * This generator is suitable for simple models where agents share the same initial configuration.
 */
public class DefaultAgentGenerator extends AgentGenerator {

    /** Internal counter to ensure each agent has a unique name */
    private static int agentCount = 0;

    /**
     * Generates a single agent with a unique name and a deep copy of the base attribute set.
     *
     * @param modelSettings the model settings containing the base attribute set for agents
     * @return a new {@link Agent} with copied attributes and a unique name
     */
    @Override
    public Agent generateAgent(ModelSettings modelSettings) {
        // Copy the base attributes to ensure each agent has its own instance
        AttributeSetCollection agentAttributeSetCollection =
                modelSettings.getBaseAgentAttributeSetCollection().deepCopy();

        // Create and name the agent uniquely
        Agent newAgent = new Agent("Agent_" + agentCount, agentAttributeSetCollection);
        agentCount++;

        return newAgent;
    }
}
