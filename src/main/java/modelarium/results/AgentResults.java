package modelarium.results;

import modelarium.agents.sets.AgentSet;

/**
 * A concrete results container for a set of agents.
 *
 * <p>Wraps an {@link AgentSet} into a {@link ModelElementResults} structure,
 * enabling easy access to recorded properties and events for all agents over time.
 */
public class AgentResults extends ModelElementResults {

    /**
     * Constructs agent results from a given agent set.
     *
     * @param agentSet the set of agents whose results will be stored and accessed
     */
    public AgentResults(AgentSet agentSet) {
        super(agentSet.getAsList());
    }
}
