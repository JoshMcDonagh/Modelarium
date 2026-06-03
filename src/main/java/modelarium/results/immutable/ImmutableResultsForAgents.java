package modelarium.results.immutable;

import modelarium.results.ResultsForAgents;
import modelarium.results.mutable.MutableResultsForAgents;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ImmutableResultsForAgents implements ResultsForAgents {
    private final MutableResultsForAgents results;

    public ImmutableResultsForAgents(MutableResultsForAgents results) {
        this.results = results;
    }

    @Override
    public int agentLogCount() {
        return results.agentLogCount();
    }

    @Override
    public int attributeSetLogCount(String agentName) {
        return results.attributeSetLogCount(agentName);
    }

    @Override
    public int attributeLogCount(String agentName, String attributeSetName) {
        return results.attributeLogCount(agentName, attributeSetName);
    }

    @Override
    public List<Object> attributeLogs(String agentName, String attributeSetName, String attributeName) {
        return Collections.unmodifiableList(results.attributeLogs(agentName, attributeSetName, attributeName));
    }

    @Override
    public <T> List<T> attributeLogs(String agentName, String attributeSetName, String attributeName, Class<T> type) {
        return Collections.unmodifiableList(results.attributeLogs(agentName, attributeSetName, attributeName, type));
    }

    @Override
    public Map<String, List<Object>> attributeSetLogs(String agentName, String attributeSetName) {
        return unmodifiableMapOfLists(results.attributeSetLogs(agentName, attributeSetName));
    }

    @Override
    public Map<String, Map<String, List<Object>>> agentLogs(String agentName) {
        return unmodifiableNestedMapOfLists(results.agentLogs(agentName));
    }

    @Override
    public Map<String, Map<String, Map<String, List<Object>>>> allLogs() {
        Map<String, Map<String, Map<String, List<Object>>>> original = results.allLogs();
        Map<String, Map<String, Map<String, List<Object>>>> wrapped = new HashMap<>();

        for (Map.Entry<String, Map<String, Map<String, List<Object>>>> entry : original.entrySet())
            wrapped.put(entry.getKey(), unmodifiableNestedMapOfLists(entry.getValue()));

        return Collections.unmodifiableMap(wrapped);
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
