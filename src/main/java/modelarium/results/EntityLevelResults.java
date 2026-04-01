package modelarium.results;

import modelarium.Entity;
import modelarium.logging.EntityLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityLevelResults {

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

    public List<Object> getAttributeLogForEntity(String entityName, String attributeSetName, String attributeName) {
        return entityLogList.get(entityLogIndexMap.get(entityName)).get(attributeSetName).getValues(attributeName);
    }

    public int size() {
        return entityLogList.size();
    }

    public void disconnectDatabases() {
        for (EntityLog entityLog : entityLogList)
            entityLog.disconnectDatabases();

        entityLogList.clear();
        entityLogIndexMap.clear();
    }
}
