package modelarium.results.immutable;

import modelarium.results.ResultsForEnvironment;
import modelarium.results.mutable.MutableResultsForEnvironment;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for providing a read-only view of the model's environment-level results.
 *
 * <p>This class wraps a {@link MutableResultsForEnvironment} instance so that the environment's logs can be queried
 * without being modifiable, with every returned list and map wrapped as unmodifiable.
 */
public final class ImmutableResultsForEnvironment implements ResultsForEnvironment {

    /** The mutable environment results this read-only view wraps */
    private final MutableResultsForEnvironment results;

    /**
     * Constructs a new immutable environment results view wrapping the specified mutable results.
     *
     * @param results the mutable environment results to provide a read-only view of
     */
    public ImmutableResultsForEnvironment(MutableResultsForEnvironment results) {
        this.results = results;
    }

    /**
     * Returns the number of attribute set logs recorded for the environment.
     *
     * @return the environment's attribute set log count
     */
    @Override
    public int attributeSetLogCount() {
        return results.attributeSetLogCount();
    }

    /**
     * Returns the number of attribute logs recorded in the environment's named attribute set.
     *
     * @param attributeSetName the name of the attribute set whose logs to count
     * @return the attribute set's attribute log count
     */
    @Override
    public int attributeLogCount(String attributeSetName) {
        return results.attributeLogCount(attributeSetName);
    }

    /**
     * Retrieves the values logged for a single attribute of the environment.
     *
     * @param attributeSetName the name of the attribute set the attribute belongs to
     * @param attributeName the name of the attribute whose values to retrieve
     * @return an unmodifiable list of the attribute's logged values, one per tick
     */
    @Override
    public List<Object> attributeLogs(String attributeSetName, String attributeName) {
        return Collections.unmodifiableList(results.attributeLogs(attributeSetName, attributeName));
    }

    /**
     * Retrieves the values logged for a single attribute of the environment, cast to the given type.
     *
     * @param attributeSetName the name of the attribute set the attribute belongs to
     * @param attributeName the name of the attribute whose values to retrieve
     * @param type the class to cast each logged value to
     * @param <T> the type the logged values are returned as
     * @return an unmodifiable list of the attribute's logged values, one per tick
     */
    @Override
    public <T> List<T> attributeLogs(String attributeSetName, String attributeName, Class<T> type) {
        return Collections.unmodifiableList(results.attributeLogs(attributeSetName, attributeName, type));
    }

    /**
     * Retrieves the values logged for every attribute in one of the environment's attribute sets.
     *
     * @param attributeSetName the name of the attribute set whose logs to retrieve
     * @return an unmodifiable map from attribute name to that attribute's logged values
     */
    @Override
    public Map<String, List<Object>> attributeSetLogs(String attributeSetName) {
        return unmodifiableMapOfLists(results.attributeSetLogs(attributeSetName));
    }

    /**
     * Retrieves the values logged for every attribute of the environment.
     *
     * @return an unmodifiable map from attribute set name to a map from attribute name to that attribute's logged
     *         values
     */
    @Override
    public Map<String, Map<String, List<Object>>> environmentLogs() {
        return unmodifiableNestedMapOfLists(results.environmentLogs());
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
