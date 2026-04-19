package modelarium.results;

import modelarium.results.immutable.ImmutableResultsForAgents;
import modelarium.results.mutable.MutableResultsForAgents;

import java.util.List;
import java.util.Map;

public sealed interface ResultsForAgents extends ResultsForEntities
        permits MutableResultsForAgents, ImmutableResultsForAgents {
    int agentLogCount();
    int attributeSetLogCount(String agentName);
    int attributeLogCount(String agentName, String attributeSetName);
    List<Object> attributeLogs(String agentName, String attributeSetName, String attributeName);
    <T> List<T> attributeLogs(String agentName, String attributeSetName, String attributeName, Class<T> type);
    Map<String, List<Object>> attributeSetLogs(String agentName, String attributeSetName);
    Map<String, Map<String, List<Object>>> agentLogs(String agentName);
    Map<String, Map<String, Map<String, List<Object>>>> allLogs();
}
