package modelarium.results.mutable;

import modelarium.entities.Entity;
import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.contexts.Context;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.EntityLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MutableResultsForEntities<C extends Context, A extends AttributeSet<C>, L extends AttributeSetLog<C>> {

    private final List<EntityLog<C,A,L>> entityLogList = new ArrayList<>();
    private final Map<String, Integer> entityLogIndexMap = new HashMap<>();

    MutableResultsForEntities(List<? extends Entity<C,A,L>> entities) {
        for (int i = 0; i < entities.size(); i++) {
            Entity<C,A,L> entity = entities.get(i);
            entityLogList.add(entity.getLog());
            entityLogIndexMap.put(entity.name(), i);
        }
    }

    MutableResultsForEntities(Entity<C,A,L> entity) {
        entityLogList.add(entity.getLog());
        entityLogIndexMap.put(entity.name(), 0);
    }

    MutableResultsForEntities(MutableResultsForEntities<C,A,L> other) {
        this.entityLogList.addAll(other.entityLogList);
        this.entityLogIndexMap.putAll(other.entityLogIndexMap);
    }

    public void mergeWith(MutableResultsForEntities<C,A,L> other) {
        int originalLogListSize = entityLogList.size();
        entityLogList.addAll(other.entityLogList);
        for (Map.Entry<String, Integer> otherIndexMapEntry : other.entityLogIndexMap.entrySet()) {
            String entityName = otherIndexMapEntry.getKey();
            int oldLogIndex = otherIndexMapEntry.getValue();
            int newLogIndex = originalLogListSize + oldLogIndex;
            entityLogIndexMap.put(entityName, newLogIndex);
        }
    }

    private EntityLog<C,A,L> getEntityLog(String entityName) {
        return entityLogList.get(entityLogIndexMap.get(entityName));
    }

    protected List<Object> getLogsForEntityAttribute(String entityName, String attributeSetName, String attributeName) {
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
        L attributeSetLog = getEntityLog(entityName).get(attributeSetName);
        List<String> attributeNamesList = attributeSetLog.getAttributeNamesList();
        Map<String, List<Object>> logsForEntityAttributeSet = new HashMap<>();

        for (String attributeName : attributeNamesList)
            logsForEntityAttributeSet.put(attributeName, attributeSetLog.getValues(attributeName));

        return logsForEntityAttributeSet;
    }

    protected Map<String, Map<String, List<Object>>> getLogsForEntityAsMap(String entityName) {
        EntityLog<C,A,L> entityLog = getEntityLog(entityName);
        Map<String, Map<String, List<Object>>> logsForEntity = new HashMap<>();

        for (int i = 0; i < entityLog.attributeSetLogCount(); i++) {
            L attributeSetLog = entityLog.get(i);
            String attributeSetName = attributeSetLog.getAttributeSetName();
            logsForEntity.put(attributeSetName, getLogsForEntityAttributeSetAsMap(entityName, attributeSetName));
        }

        return logsForEntity;
    }

    protected Map<String, Map<String, Map<String, List<Object>>>> allLogs() {
        Map<String, Map<String, Map<String, List<Object>>>> allLogs = new HashMap<>();
        for (EntityLog<C,A,L> entityLog : entityLogList) {
            String entityName = entityLog.getEntityName();
            allLogs.put(entityName, getLogsForEntityAsMap(entityName));
        }
        return allLogs;
    }

    public void disconnectDatabases() {
        for (EntityLog<C,A,L> entityLog : entityLogList)
            entityLog.disconnectDatabases();

        entityLogList.clear();
        entityLogIndexMap.clear();
    }
}
