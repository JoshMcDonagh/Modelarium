package modelarium.results.immutable;

import modelarium.results.ResultsForAgents;
import modelarium.results.mutable.MutableResultsForAgents;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for providing a read-only view of the model's agent-level results.
 *
 * <p>This class wraps a {@link MutableResultsForAgents} instance so that agent logs can be queried without being
 * modifiable, with every returned list and map wrapped as unmodifiable.
 */
public final class ImmutableResultsForAgents implements ResultsForAgents {

    /** The mutable agent results this read-only view wraps */
    private final MutableResultsForAgents results;

    /**
     * Constructs a new immutable agent results view wrapping the specified mutable results.
     *
     * @param results the mutable agent results to provide a read-only view of
     */
    public ImmutableResultsForAgents(MutableResultsForAgents results) {
        this.results = results;
    }

    /**
     * Returns the number of agents with logs in the results.
     *
     * @return the agent log count
     */
    @Override
    public int agentLogCount() {
        return results.agentLogCount();
    }

    /**
     * Returns the number of attribute set logs recorded for the named agent.
     *
     * @param agentName the name of the agent whose logs to count
     * @return the agent's attribute set log count
     */
    @Override
    public int attributeSetLogCount(String agentName) {
        return results.attributeSetLogCount(agentName);
    }

    /**
     * Returns the number of attribute logs recorded in the named agent's named attribute set.
     *
     * @param agentName the name of the agent whose logs to count
     * @param attributeSetName the name of the attribute set whose logs to count
     * @return the attribute set's attribute log count
     */
    @Override
    public int attributeLogCount(String agentName, String attributeSetName) {
        return results.attributeLogCount(agentName, attributeSetName);
    }

    /**
     * Retrieves the values logged for a single attribute of an agent.
     *
     * @param agentName the name of the agent the attribute belongs to
     * @param attributeSetName the name of the attribute set the attribute belongs to
     * @param attributeName the name of the attribute whose values to retrieve
     * @return an unmodifiable list of the attribute's logged values, one per tick
     */
    @Override
    public List<Object> attributeLogs(String agentName, String attributeSetName, String attributeName) {
        return Collections.unmodifiableList(results.attributeLogs(agentName, attributeSetName, attributeName));
    }

    /**
     * Retrieves the values logged for a single attribute of an agent, cast to the given type.
     *
     * @param agentName the name of the agent the attribute belongs to
     * @param attributeSetName the name of the attribute set the attribute belongs to
     * @param attributeName the name of the attribute whose values to retrieve
     * @param type the class to cast each logged value to
     * @param <T> the type the logged values are returned as
     * @return an unmodifiable list of the attribute's logged values, one per tick
     */
    @Override
    public <T> List<T> attributeLogs(String agentName, String attributeSetName, String attributeName, Class<T> type) {
        return Collections.unmodifiableList(results.attributeLogs(agentName, attributeSetName, attributeName, type));
    }

    /**
     * Retrieves the values logged for every attribute in one of an agent's attribute sets.
     *
     * @param agentName the name of the agent the attribute set belongs to
     * @param attributeSetName the name of the attribute set whose logs to retrieve
     * @return an unmodifiable map from attribute name to that attribute's logged values
     */
    @Override
    public Map<String, List<Object>> attributeSetLogs(String agentName, String attributeSetName) {
        return unmodifiableMapOfLists(results.attributeSetLogs(agentName, attributeSetName));
    }

    /**
     * Retrieves the values logged for every attribute of an agent.
     *
     * @param agentName the name of the agent whose logs to retrieve
     * @return an unmodifiable map from attribute set name to a map from attribute name to that attribute's logged
     *         values
     */
    @Override
    public Map<String, Map<String, List<Object>>> agentLogs(String agentName) {
        return unmodifiableNestedMapOfLists(results.agentLogs(agentName));
    }

    /**
     * Retrieves the values logged for every attribute of every agent in the model.
     *
     * @return an unmodifiable map from agent name to a map from attribute set name to a map from attribute name to
     *         that attribute's logged values
     */
    @Override
    public Map<String, Map<String, Map<String, List<Object>>>> allLogs() {
        Map<String, Map<String, Map<String, List<Object>>>> original = results.allLogs();
        Map<String, Map<String, Map<String, List<Object>>>> wrapped = new HashMap<>();

        for (Map.Entry<String, Map<String, Map<String, List<Object>>>> entry : original.entrySet())
            wrapped.put(entry.getKey(), unmodifiableNestedMapOfLists(entry.getValue()));

        return Collections.unmodifiableMap(wrapped);
    }

    /**
     * Wraps a map of lists so that neither the map nor any of its lists can be modified.
     *
     * @param map the map of logged values to wrap
     * @return an unmodifiable view of the map with each list wrapped as unmodifiable
     */
    private static Map<String, List<Object>> unmodifiableMapOfLists(Map<String, List<Object>> map) {
        Map<String, List<Object>> wrapped = new HashMap<>();

        for (Map.Entry<String, List<Object>> entry : map.entrySet())
            wrapped.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));

        return Collections.unmodifiableMap(wrapped);
    }

    /**
     * Wraps a nested map of lists so that no level of the structure can be modified.
     *
     * @param map the nested map of logged values to wrap
     * @return an unmodifiable view of the map with each inner map and list wrapped as unmodifiable
     */
    private static Map<String, Map<String, List<Object>>> unmodifiableNestedMapOfLists(Map<String, Map<String, List<Object>>> map) {
        Map<String, Map<String, List<Object>>> wrapped = new HashMap<>();

        for (Map.Entry<String, Map<String, List<Object>>> entry : map.entrySet())
            wrapped.put(entry.getKey(), unmodifiableMapOfLists(entry.getValue()));

        return Collections.unmodifiableMap(wrapped);
    }

}
