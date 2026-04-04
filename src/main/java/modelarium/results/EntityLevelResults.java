package modelarium.results;

import modelarium.Entity;
import modelarium.logging.AttributeSetLog;
import modelarium.logging.EntityLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EntityLevelResults {

    private final List<EntityLog> entityLogList = new ArrayList<>();
    private final Map<String, Integer> entityLogIndexMap = new HashMap<>();

    public EntityLevelResults(List<? extends Entity> entities) {
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entityLogList.add(entity.getLog());
            entityLogIndexMap.put(entity.name(), i);
        }
    }

    EntityLevelResults(Entity entity) {
        entityLogList.add(entity.getLog());
        entityLogIndexMap.put(entity.name(), 0);
    }

    public void mergeWith(EntityLevelResults other) {
        int originalLogListSize = entityLogList.size();
        entityLogList.addAll(other.entityLogList);
        for (Map.Entry<String, Integer> otherIndexMapEntry : other.entityLogIndexMap.entrySet()) {
            String entityName = otherIndexMapEntry.getKey();
            int oldLogIndex = otherIndexMapEntry.getValue();
            int newLogIndex = originalLogListSize + oldLogIndex;
            entityLogIndexMap.put(entityName, newLogIndex);
        }
    }

    private EntityLog getEntityLog(String entityName) {
        return entityLogList.get(entityLogIndexMap.get(entityName));
    }

    protected List<Object> getLogForEntityAttribute(String entityName, String attributeSetName, String attributeName) {
        return getEntityLog(entityName).get(attributeSetName).getValues(attributeName);
    }

    protected int entityLogCount() {
        return entityLogList.size();
    }

    protected int entityAttributeSetLogCount(String entityName) {
        return getEntityLog(entityName).attributeSetLogCount();
    }

    protected int entityAttributeSetAttributeLogCount(String entityName, String attributeSetName) {
        return getEntityLog(entityName).get(attributeSetName).attributeLogCount();
    }

    protected Map<String, List<Object>> getLogsForEntityAttributeSetAsMap(String entityName, String attributeSetName) {
        AttributeSetLog attributeSetLog = getEntityLog(entityName).get(attributeSetName);
        List<String> attributeNamesList = attributeSetLog.getAttributeNamesList();
        Map<String, List<Object>> logsForEntityAttributeSet = new HashMap<>();

        for (String attributeName : attributeNamesList)
            logsForEntityAttributeSet.put(attributeName, attributeSetLog.getValues(attributeName));

        return logsForEntityAttributeSet;
    }

    protected Map<String, Map<String, List<Object>>> getLogsForEntityAsMap(String entityName) {
        EntityLog entityLog = getEntityLog(entityName);
        Map<String, Map<String, List<Object>>> logsForEntity = new HashMap<>();

        for (int i = 0; i < entityLog.attributeSetLogCount(); i++) {
            AttributeSetLog attributeSetLog = entityLog.get(i);
            String attributeSetName = attributeSetLog.getAttributeSetName();
            logsForEntity.put(attributeSetName, getLogsForEntityAttributeSetAsMap(entityName, attributeSetName));
        }

        return logsForEntity;
    }

    protected Map<String, Map<String, Map<String, List<Object>>>> getAllLogsAsMap() {
        Map<String, Map<String, Map<String, List<Object>>>> allLogs = new HashMap<>();
        for (EntityLog entityLog : entityLogList) {
            String entityName = entityLog.getEntityName();
            allLogs.put(entityName, getLogsForEntityAsMap(entityName));
        }
        return allLogs;
    }

    public void disconnectDatabases() {
        for (EntityLog entityLog : entityLogList)
            entityLog.disconnectDatabases();

        entityLogList.clear();
        entityLogIndexMap.clear();
    }
}
