package modelarium.results;

import modelarium.results.immutable.ImmutableResultsForAgents;
import modelarium.results.mutable.MutableResultsForAgents;

import java.util.List;
import java.util.Map;

/**
 * Interface for providing access to the logged attribute values of the model's agents.
 *
 * <p>This interface exposes an agent's logs at every level of granularity, from a single attribute's values up to
 * the logs of every agent in the model, keyed by agent, attribute set and attribute name.
 */
public sealed interface ResultsForAgents extends ResultsForEntities
        permits MutableResultsForAgents, ImmutableResultsForAgents {

    /**
     * Returns the number of agents with logs in the results.
     *
     * @return the agent log count
     */
    int agentLogCount();

    /**
     * Returns the number of attribute set logs recorded for the named agent.
     *
     * @param agentName the name of the agent whose logs to count
     * @return the agent's attribute set log count
     */
    int attributeSetLogCount(String agentName);

    /**
     * Returns the number of attribute logs recorded in the named agent's named attribute set.
     *
     * @param agentName the name of the agent whose logs to count
     * @param attributeSetName the name of the attribute set whose logs to count
     * @return the attribute set's attribute log count
     */
    int attributeLogCount(String agentName, String attributeSetName);

    /**
     * Retrieves the values logged for a single attribute of an agent.
     *
     * @param agentName the name of the agent the attribute belongs to
     * @param attributeSetName the name of the attribute set the attribute belongs to
     * @param attributeName the name of the attribute whose values to retrieve
     * @return the attribute's logged values, one per tick
     */
    List<Object> attributeLogs(String agentName, String attributeSetName, String attributeName);

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
    <T> List<T> attributeLogs(String agentName, String attributeSetName, String attributeName, Class<T> type);

    /**
     * Retrieves the values logged for every attribute in one of an agent's attribute sets.
     *
     * @param agentName the name of the agent the attribute set belongs to
     * @param attributeSetName the name of the attribute set whose logs to retrieve
     * @return a map from attribute name to that attribute's logged values
     */
    Map<String, List<Object>> attributeSetLogs(String agentName, String attributeSetName);

    /**
     * Retrieves the values logged for every attribute of an agent.
     *
     * @param agentName the name of the agent whose logs to retrieve
     * @return a map from attribute set name to a map from attribute name to that attribute's logged values
     */
    Map<String, Map<String, List<Object>>> agentLogs(String agentName);

    /**
     * Retrieves the values logged for every attribute of every agent in the model.
     *
     * @return a map from agent name to a map from attribute set name to a map from attribute name to that
     *         attribute's logged values
     */
    Map<String, Map<String, Map<String, List<Object>>>> allLogs();
}
