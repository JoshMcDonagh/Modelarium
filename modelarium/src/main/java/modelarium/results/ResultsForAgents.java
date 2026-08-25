package modelarium.results;

import modelarium.entities.agentsets.AgentSet;
import modelarium.entities.attributes.sets.AgentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.EntityLog;
import modelarium.results.readonly.ReadOnlyResultsForAgents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A concrete results container for a set of agents.
 *
 * <p>Wraps an {@link AgentSet} into a {@link ResultsForEntities} structure,
 * enabling easy access to recorded properties and events for all agents over time.
 */
public final class ResultsForAgents extends ResultsForEntities<AgentSimulationContext, AgentContext, AgentAttributeSet, AttributeSetLog<AgentSimulationContext>> {

    /** The immutable version of this mutable agent-level results */
    private ReadOnlyResultsForAgents immutableResultsForAgents = null;

    /**
     * Constructs agent results from a given agent set.
     *
     * @param agentSet the set of agents whose results will be stored and accessed
     */
    public ResultsForAgents(AgentSet agentSet) {
        super(agentSet.getAsList());
    }

    /**
     * Returns the number of agents with logs in the results.
     *
     * @return the agent log count
     */
    public int agentLogCount() {
        return entityLogCount();
    }

    /**
     * Returns the number of attribute set logs recorded for the named agent.
     *
     * @param agentName the name of the agent whose logs to count
     * @return the agent's attribute set log count
     */
    public int attributeSetLogCount(String agentName) {
        return entityAttributeSetLogCount(agentName);
    }

    /**
     * Returns the number of attribute logs recorded in the named agent's named attribute set.
     *
     * @param agentName the name of the agent whose logs to count
     * @param attributeSetName the name of the attribute set whose logs to count
     * @return the attribute set's attribute log count
     */
    public int attributeLogCount(String agentName, String attributeSetName) {
        return entityAttributeSetAttributeLogCount(agentName, attributeSetName);
    }

    /**
     * Retrieves the values logged for a single attribute of an agent.
     *
     * @param agentName the name of the agent the attribute belongs to
     * @param attributeSetName the name of the attribute set the attribute belongs to
     * @param attributeName the name of the attribute whose values to retrieve
     * @return the attribute's logged values, one per tick
     */
    public List<Object> attributeLogs(String agentName, String attributeSetName, String attributeName) {
        return getLogsForEntityAttribute(agentName, attributeSetName, attributeName);
    }

    /**
     * Retrieves the values logged for a single attribute of an agent, cast to the given type.
     *
     * @param agentName the name of the agent the attribute belongs to
     * @param attributeSetName the name of the attribute set the attribute belongs to
     * @param attributeName the name of the attribute whose values to retrieve
     * @param type the class to cast each logged value to
     * @param <T> the type the logged values are returned as
     * @return the attribute's logged values, one per tick
     */
    public <T> List<T> attributeLogs(String agentName, String attributeSetName, String attributeName, Class<T> type) {
        List<Object> raw = getLogsForEntityAttribute(agentName, attributeSetName, attributeName);
        List<T> typed = new ArrayList<>(raw.size());
        for (Object value : raw)
            typed.add(type.cast(value));
        return typed;
    }

    /**
     * Retrieves the values logged for every attribute in one of an agent's attribute sets.
     *
     * @param agentName the name of the agent the attribute set belongs to
     * @param attributeSetName the name of the attribute set whose logs to retrieve
     * @return a map from attribute name to that attribute's logged values
     */
    public Map<String, List<Object>> attributeSetLogs(String agentName, String attributeSetName) {
        return getLogsForEntityAttributeSetAsMap(agentName, attributeSetName);
    }

    /**
     * Retrieves the values logged for every attribute of an agent.
     *
     * @param agentName the name of the agent whose logs to retrieve
     * @return a map from attribute set name to a map from attribute name to that attribute's logged values
     */
    public Map<String, Map<String, List<Object>>> agentLogs(String agentName) {
        return getLogsForEntityAsMap(agentName);
    }

    /**
     * Retrieves the values logged for every attribute of every agent in the model.
     *
     * @return a map from agent name to a map from attribute set name to a map from attribute name to that
     *         attribute's logged values
     */
    @Override
    public Map<String, Map<String, Map<String, List<Object>>>> allLogs() {
        return super.allLogs();
    }

    /**
     * Exports the results of the agents to the given export path
     *
     * @param exportPath the path results are to be exported to
     */
    @Override
    void export(Path exportPath) {
        List<EntityLog<
                        AgentSimulationContext,
                        AgentContext,
                AgentAttributeSet,
                        AttributeSetLog<AgentSimulationContext>
                        >>
                agentLogList = getEntityLogList();

        Path agentsResultsExportPath = exportPath.resolve("agent");

        try {
            Files.createDirectories(agentsResultsExportPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create path: " + agentsResultsExportPath, e);
        }

        for (EntityLog<
                AgentSimulationContext,
                AgentContext,
                AgentAttributeSet,
                AttributeSetLog<AgentSimulationContext>
                > agentLog : agentLogList) {
            String agentName = agentLog.getEntityName();
            Path agentResultsExportPath = agentsResultsExportPath.resolve(agentName);

            try {
                Files.createDirectories(agentResultsExportPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create path: " + agentResultsExportPath, e);
            }

            exportEntityResults(agentLog, agentResultsExportPath);
        }
    }

    /**
     * Returns a read-only view of these agent results.
     *
     * @return a new {@link ReadOnlyResultsForAgents} instance wrapping these results
     */
    public ReadOnlyResultsForAgents getAsImmutable() {
        if (immutableResultsForAgents == null)
            immutableResultsForAgents = new ReadOnlyResultsForAgents(this);

        return immutableResultsForAgents;
    }
}
