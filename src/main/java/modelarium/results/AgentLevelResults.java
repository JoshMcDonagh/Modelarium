package modelarium.results;

import modelarium.agents.sets.AgentSet;

import java.util.List;
import java.util.Map;

/**
 * A concrete results container for a set of agents.
 *
 * <p>Wraps an {@link AgentSet} into a {@link EntityLevelResults} structure,
 * enabling easy access to recorded properties and events for all agents over time.
 */
public class AgentLevelResults extends EntityLevelResults {

    /**
     * Constructs agent results from a given agent set.
     *
     * @param agentSet the set of agents whose results will be stored and accessed
     */
    public AgentLevelResults(AgentSet agentSet) {
        super(agentSet.getAsList());
    }

    public List<Object> getLogForAgentAttribute(String agentName, String attributeSetName, String attributeName) {
        return getLogForEntityAttribute(agentName, attributeSetName, attributeName);
    }

    public int agentLogCount() {
        return entityLogCount();
    }

    public int AgentAttributeSetLogCount(String agentName) {
        return entityAttributeSetLogCount(agentName);
    }

    public int AgentAttributeSetAttributeLogCount(String agentName, String attributeSetName) {
        return entityAttributeSetAttributeLogCount(agentName, attributeSetName);
    }

    public Map<String, List<Object>> getLogsForAgentAttributeSetAsMap(String agentName, String attributeSetName) {
        return getLogsForEntityAttributeSetAsMap(agentName, attributeSetName);
    }

    public Map<String, Map<String, List<Object>>> getLogsForAgentAsMap(String agentName) {
        return getLogsForEntityAsMap(agentName);
    }

    @Override
    public Map<String, Map<String, Map<String, List<Object>>>> getAllLogsAsMap() {
        return super.getAllLogsAsMap();
    }
}
