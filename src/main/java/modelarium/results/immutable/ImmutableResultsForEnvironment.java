package modelarium.results.immutable;

import modelarium.results.ResultsForEnvironment;
import modelarium.results.mutable.MutableResultsForEnvironment;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ImmutableResultsForEnvironment implements ResultsForEnvironment {
    private final MutableResultsForEnvironment results;

    public ImmutableResultsForEnvironment(MutableResultsForEnvironment results) {
        this.results = results;
    }

    @Override
    public int attributeSetLogCount() {
        return results.attributeSetLogCount();
    }

    @Override
    public int attributeLogCount(String attributeSetName) {
        return results.attributeLogCount(attributeSetName);
    }

    @Override
    public List<Object> attributeLogs(String attributeSetName, String attributeName) {
        return Collections.unmodifiableList(results.attributeLogs(attributeSetName, attributeName));
    }

    @Override
    public <T> List<T> attributeLogs(String attributeSetName, String attributeName, Class<T> type) {
        return Collections.unmodifiableList(results.attributeLogs(attributeSetName, attributeName, type));
    }

    @Override
    public Map<String, List<Object>> attributeSetLogs(String attributeSetName) {
        return unmodifiableMapOfLists(results.attributeSetLogs(attributeSetName));
    }

    @Override
    public Map<String, Map<String, List<Object>>> environmentLogs() {
        return unmodifiableNestedMapOfLists(results.environmentLogs());
    }

    private static Map<String, List<Object>> unmodifiableMapOfLists(Map<String, List<Object>> map) {
        Map<String, List<Object>> wrapped = new HashMap<>();

        for (Map.Entry<String, List<Object>> entry : map.entrySet())
            wrapped.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));

        return Collections.unmodifiableMap(wrapped);
    }

    private static Map<String, Map<String, List<Object>>> unmodifiableNestedMapOfLists(Map<String, Map<String, List<Object>>> map) {
        Map<String, Map<String, List<Object>>> wrapped = new HashMap<>();

        for (Map.Entry<String, Map<String, List<Object>>> entry : map.entrySet())
            wrapped.put(entry.getKey(), unmodifiableMapOfLists(entry.getValue()));

        return Collections.unmodifiableMap(wrapped);
    }
}
