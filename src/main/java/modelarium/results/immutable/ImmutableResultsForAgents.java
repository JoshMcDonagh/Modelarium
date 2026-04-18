package modelarium.results.immutable;

import modelarium.results.ResultsForAgents;
import modelarium.results.mutable.MutableResultsForAgents;

import java.util.List;
import java.util.Map;

public class ImmutableResultsForAgents implements ResultsForAgents {
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
        return results.attributeLogs(agentName, attributeSetName, attributeName, type);
    }

    @Override
    public Map<String, List<Object>> attributeSetLogs(String agentName, String attributeSetName) {
        return results.attributeSetLogs(agentName, attributeSetName);
    }

    @Override
    public Map<String, Map<String, List<Object>>> agentLogs(String agentName) {
        return results.agentLogs(agentName);
    }

    @Override
    public Map<String, Map<String, Map<String, List<Object>>>> allLogs() {
        return results.allLogs();
    }
}
