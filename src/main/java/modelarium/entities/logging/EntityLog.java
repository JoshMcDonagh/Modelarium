package modelarium.entities.logging;

import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.contexts.Context;
import modelarium.entities.contexts.SimulationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityLog<SC extends SimulationContext, C extends Context, AS extends AttributeSet<SC,C>, ASL extends AttributeSetLog<SC>> {
    private final String entityName;
    private final List<ASL> attributeSetLogList = new ArrayList<>();
    private final Map<String, Integer> attributeSetLogIndexList = new HashMap<>();

    public EntityLog(String entityName, List<AS> attributeSets) {
        this.entityName = entityName;
        for (int i = 0; i < attributeSets.size(); i++) {
            AS attributeSet = attributeSets.get(i);
            // noinspection unchecked
            attributeSetLogList.add((ASL) attributeSet.getLog());
            attributeSetLogIndexList.put(attributeSet.name(), i);
        }
    }

    public String getEntityName() {
        return entityName;
    }

    public ASL get(int attributeSetIndex) {
        return attributeSetLogList.get(attributeSetIndex);
    }

    public ASL get(String attributeSetName) {
        return get(attributeSetLogIndexList.get(attributeSetName));
    }

    public int attributeSetLogCount() {
        return attributeSetLogList.size();
    }

    public void disconnectDatabases() {
        for (ASL attributeSetLog : attributeSetLogList)
            attributeSetLog.disconnectDatabase();

        attributeSetLogList.clear();
        attributeSetLogIndexList.clear();
    }
}
