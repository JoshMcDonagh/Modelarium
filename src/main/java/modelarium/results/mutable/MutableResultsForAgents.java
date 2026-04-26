package modelarium.results.mutable;

import modelarium.entities.agents.AgentSet;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.results.ResultsForAgents;
import modelarium.results.immutable.ImmutableResultsForAgents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A concrete results container for a set of agents.
 *
 * <p>Wraps an {@link AgentSet} into a {@link MutableResultsForEntities} structure,
 * enabling easy access to recorded properties and events for all agents over time.
 */
public final class MutableResultsForAgents extends MutableResultsForEntities<AgentSimulationContext, AgentContext, AgentAttributeSet, AttributeSetLog<AgentSimulationContext>> implements ResultsForAgents {
    /**
     * Constructs agent results from a given agent set.
     *
     * @param agentSet the set of agents whose results will be stored and accessed
     */
    public MutableResultsForAgents(AgentSet agentSet) {
        super(agentSet.getAsList());
    }

    @Override
    public int agentLogCount() {
        return entityLogCount();
    }

    @Override
    public int attributeSetLogCount(String agentName) {
        return entityAttributeSetLogCount(agentName);
    }

    @Override
    public int attributeLogCount(String agentName, String attributeSetName) {
        return entityAttributeSetAttributeLogCount(agentName, attributeSetName);
    }

    @Override
    public List<Object> attributeLogs(String agentName, String attributeSetName, String attributeName) {
        return getLogsForEntityAttribute(agentName, attributeSetName, attributeName);
    }

    @Override
    public <T> List<T> attributeLogs(String agentName, String attributeSetName, String attributeName, Class<T> type) {
        List<Object> raw = getLogsForEntityAttribute(agentName, attributeSetName, attributeName);
        List<T> typed = new ArrayList<>(raw.size());
        for (Object value : raw)
            typed.add(type.cast(value));
        return typed;
    }

    @Override
    public Map<String, List<Object>> attributeSetLogs(String agentName, String attributeSetName) {
        return getLogsForEntityAttributeSetAsMap(agentName, attributeSetName);
    }

    @Override
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
