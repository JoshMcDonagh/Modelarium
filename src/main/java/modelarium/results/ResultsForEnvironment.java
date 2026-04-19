package modelarium.results;

import modelarium.results.immutable.ImmutableResultsForEnvironment;
import modelarium.results.mutable.MutableResultsForEnvironment;

import java.util.List;
import java.util.Map;

public sealed interface ResultsForEnvironment extends ResultsForEntities
        permits MutableResultsForEnvironment, ImmutableResultsForEnvironment {
    int attributeSetLogCount();
    int attributeLogCount(String attributeSetName);
    List<Object> attributeLogs(String attributeSetName, String attributeName);
    <T> List<T> attributeLogs(String attributeSetName, String attributeName, Class<T> type);
    Map<String, List<Object>> attributeSetLogs(String attributeSetName);
    Map<String, Map<String, List<Object>>> environmentLogs();
}
