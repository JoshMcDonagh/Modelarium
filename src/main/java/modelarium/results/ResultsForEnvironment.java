package modelarium.results;

import java.util.List;
import java.util.Map;

public interface ResultsForEnvironment extends ResultsForEntities {
    int attributeSetLogCount();
    int attributeSetLogCount(String attributeSetName);
    List<Object> attributeLogs(String attributeSetName, String attributeName);
    <T> List<T> attributeLogs(String attributeSetName, String attributeName, Class<T> type);
    Map<String, List<Object>> attributeSetLogs(String attributeSetName);
    Map<String, Map<String, List<Object>>> environmentLogs();
}
