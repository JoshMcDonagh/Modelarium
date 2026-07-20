package modelarium.results;

import modelarium.results.immutable.ImmutableResultsForEnvironment;
import modelarium.results.mutable.MutableResultsForEnvironment;

import java.util.List;
import java.util.Map;

/**
 * Interface for providing access to the logged attribute values of the model's environment.
 *
 * <p>This interface exposes the environment's logs at every level of granularity, from a single attribute's values
 * up to the logs of every attribute set the environment owns, keyed by attribute set and attribute name.
 */
public sealed interface ResultsForEnvironment extends ResultsForEntities
        permits MutableResultsForEnvironment, ImmutableResultsForEnvironment {

    /**
     * Returns the number of attribute set logs recorded for the environment.
     *
     * @return the environment's attribute set log count
     */
    int attributeSetLogCount();

    /**
     * Returns the number of attribute logs recorded in the environment's named attribute set.
     *
     * @param attributeSetName the name of the attribute set whose logs to count
     * @return the attribute set's attribute log count
     */
    int attributeLogCount(String attributeSetName);

    /**
     * Retrieves the values logged for a single attribute of the environment.
     *
     * @param attributeSetName the name of the attribute set the attribute belongs to
     * @param attributeName the name of the attribute whose values to retrieve
     * @return the attribute's logged values, one per tick
     */
    List<Object> attributeLogs(String attributeSetName, String attributeName);

    /**
     * Retrieves the values logged for a single attribute of the environment, cast to the given type.
     *
     * @param attributeSetName the name of the attribute set the attribute belongs to
     * @param attributeName the name of the attribute whose values to retrieve
     * @param type the class to cast each logged value to
     * @param <T> the type the logged values are returned as
     * @return the attribute's logged values, one per tick
     */
    <T> List<T> attributeLogs(String attributeSetName, String attributeName, Class<T> type);

    /**
     * Retrieves the values logged for every attribute in one of the environment's attribute sets.
     *
     * @param attributeSetName the name of the attribute set whose logs to retrieve
     * @return a map from attribute name to that attribute's logged values
     */
    Map<String, List<Object>> attributeSetLogs(String attributeSetName);

    /**
     * Retrieves the values logged for every attribute of the environment.
     *
     * @return a map from attribute set name to a map from attribute name to that attribute's logged values
     */
    Map<String, Map<String, List<Object>>> environmentLogs();
}
