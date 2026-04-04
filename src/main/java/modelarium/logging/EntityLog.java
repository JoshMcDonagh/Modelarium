package modelarium.logging;

import modelarium.attributes.AttributeSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityLog {
    private final String entityName;
    private final List<AttributeSetLog> attributeSetLogList = new ArrayList<>();
    private final Map<String, Integer> attributeSetLogIndexList = new HashMap<>();

    public EntityLog(String entityName, List<AttributeSet> attributeSets) {
        this.entityName = entityName;
        for (int i = 0; i < attributeSets.size(); i++) {
            AttributeSet attributeSet = attributeSets.get(i);
            attributeSetLogList.add(attributeSet.getLog());
            attributeSetLogIndexList.put(attributeSet.name(), i);
        }
    }

    public String getEntityName() {
        return entityName;
    }

    public AttributeSetLog get(int attributeSetIndex) {
        return attributeSetLogList.get(attributeSetIndex);
    }

    public AttributeSetLog get(String attributeSetName) {
        return get(attributeSetLogIndexList.get(attributeSetName));
    }

    public int attributeSetLogCount() {
        return attributeSetLogList.size();
    }

    public void disconnectDatabases() {
        for (AttributeSetLog attributeSetLog : attributeSetLogList)
            attributeSetLog.disconnectDatabase();

        attributeSetLogList.clear();
        attributeSetLogIndexList.clear();
    }
}
