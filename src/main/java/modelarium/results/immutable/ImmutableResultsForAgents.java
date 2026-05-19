package modelarium.results.immutable;

import modelarium.results.ResultsForAgents;
import modelarium.results.mutable.MutableResultsForAgents;

import java.util.ArrayList;
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
        return results.attributeLogs(agentName, attributeSetName, attributeName);
    }

    @Override
    public <T> List<T> attributeLogs(String agentName, String attributeSetName, String attributeName, Class<T> type) {
        return new ArrayList<>(results.attributeLogs(agentName, attributeSetName, attributeName, type));
    }

    @Override
    public Map<String, List<Object>> attributeSetLogs(String agentName, String attributeSetName) {
        Map<String, List<Object>> originalMap =  results.attributeSetLogs(agentName, attributeSetName);
        Map<String, List<Object>> newMap = new HashMap<>();

        for (Map.Entry<String, List<Object>> entry : originalMap.entrySet()) {
            String key = entry.getKey();
            List<Object> value = entry.getValue();

            newMap.put(key, new ArrayList<>(value));
        }

        return newMap;
    }

    @Override
    public Map<String, Map<String, List<Object>>> agentLogs(String agentName) {
        Map<String, Map<String, List<Object>>> originalMap =  results.agentLogs(agentName);
        Map<String, Map<String, List<Object>>> newMap = new HashMap<>();

        for (Map.Entry<String, Map<String, List<Object>>> entry : originalMap.entrySet()) {
            String key = entry.getKey();
            Map<String, List<Object>> originalNestedMap = entry.getValue();
            Map<String, List<Object>> newNestedMap =  new HashMap<>();

            for (Map.Entry<String, List<Object>> nestedEntry : originalNestedMap.entrySet()) {
                String nestedKey = nestedEntry.getKey();
                List<Object> value = nestedEntry.getValue();

                newNestedMap.put(nestedKey, new ArrayList<>(value));
            }

            newMap.put(key, new HashMap<>(newNestedMap));
        }

        return newMap;
    }

    @Override
    public Map<String, Map<String, Map<String, List<Object>>>> allLogs() {
        Map<String, Map<String, Map<String, List<Object>>>> originalMap =  results.allLogs();
        Map<String, Map<String, Map<String, List<Object>>>> newMap = new HashMap<>();

        for (Map.Entry<String, Map<String, Map<String, List<Object>>>> entry : originalMap.entrySet()) {
            String firstKey = entry.getKey();
            Map<String, Map<String, List<Object>>> originalFirstNestedMap = entry.getValue();
            Map<String, Map<String, List<Object>>> newFirstNestedMap =  new HashMap<>();

            for (Map.Entry<String, Map<String, List<Object>>> firstNestedEntry : originalFirstNestedMap.entrySet()) {
                String secondKey = firstNestedEntry.getKey();
                Map<String, List<Object>> originalSecondNestedMap = firstNestedEntry.getValue();
                Map<String, List<Object>> newSecondNestedMap =  new HashMap<>();

                for (Map.Entry<String, List<Object>> secondNestedEntry : originalSecondNestedMap.entrySet()) {
                    String thirdKey = secondNestedEntry.getKey();
                    List<Object> value = secondNestedEntry.getValue();
                    newSecondNestedMap.put(thirdKey, new ArrayList<>(value));
                }

                newFirstNestedMap.put(firstKey, new HashMap<>(newSecondNestedMap));
            }

            newMap.put(firstKey, new HashMap<>(newFirstNestedMap));
        }

        return newMap;
    }
}
