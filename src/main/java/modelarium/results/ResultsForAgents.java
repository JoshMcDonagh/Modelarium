package modelarium.results;

import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.results.immutable.ImmutableResultsForAgents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A concrete results container for a set of agents.
 *
 * <p>Wraps an {@link AgentSet} into a {@link ResultsForEntities} structure,
 * enabling easy access to recorded properties and events for all agents over time.
 */
public class ResultsForAgents extends ResultsForEntities<AgentContext, AgentAttributeSet, AttributeSetLog<AgentContext>> {
    /**
     * Constructs agent results from a given agent set.
     *
     * @param agentSet the set of agents whose results will be stored and accessed
     */
    public ResultsForAgents(AgentSet agentSet) {
        super(agentSet.getAsList());
    }

    protected ResultsForAgents(ResultsForAgents other) {
        super(other);
    }

    public int agentLogCount() {
        return entityLogCount();
    }

    public int attributeSetLogCount(String agentName) {
        return entityAttributeSetLogCount(agentName);
    }

    public int attributeLogCount(String agentName, String attributeSetName) {
        return entityAttributeSetAttributeLogCount(agentName, attributeSetName);
    }

    public List<Object> attributeLogs(String agentName, String attributeSetName, String attributeName) {
        return getLogsForEntityAttribute(agentName, attributeSetName, attributeName);
    }

    public <T> List<T> attributeLogs(String agentName, String attributeSetName, String attributeName, Class<T> type) {
        List<Object> raw = getLogsForEntityAttribute(agentName, attributeSetName, attributeName);
        List<T> typed = new ArrayList<>(raw.size());
        for (Object value : raw)
            typed.add(type.cast(value));
        return typed;
    }

    public Map<String, List<Object>> attributeSetLogs(String agentName, String attributeSetName) {
        return getLogsForEntityAttributeSetAsMap(agentName, attributeSetName);
    }

    public Map<String, Map<String, List<Object>>> agentLogs(String agentName) {
        return getLogsForEntityAsMap(agentName);
    }

    @Override
    public Map<String, Map<String, Map<String, List<Object>>>> allLogs() {
        return super.allLogs();
    }

    public ImmutableResultsForAgents getAsImmutable() {
        return new ImmutableResultsForAgents(this);
    }
}
