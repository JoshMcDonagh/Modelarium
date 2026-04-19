package modelarium.results.immutable;

import modelarium.results.ResultsForEnvironment;
import modelarium.results.mutable.MutableResultsForEnvironment;

import java.util.List;
import java.util.Map;

public class ImmutableResultsForEnvironment implements ResultsForEnvironment {
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
        return results.attributeLogs(attributeSetName, attributeName);
    }

    @Override
    public <T> List<T> attributeLogs(String attributeSetName, String attributeName, Class<T> type) {
        return results.attributeLogs(attributeSetName, attributeName, type);
    }

    @Override
    public Map<String, List<Object>> attributeSetLogs(String attributeSetName) {
        return results.attributeSetLogs(attributeSetName);
    }

    @Override
    public Map<String, Map<String, List<Object>>> environmentLogs() {
        return results.environmentLogs();
    }
}
