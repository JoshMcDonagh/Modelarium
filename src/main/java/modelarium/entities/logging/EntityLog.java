package modelarium.entities.logging;

import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.contexts.SimulationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityLog<C extends SimulationContext, A extends AttributeSet<C>, L extends AttributeSetLog<C>> {
    private final String entityName;
    private final List<L> attributeSetLogList = new ArrayList<>();
    private final Map<String, Integer> attributeSetLogIndexList = new HashMap<>();

    public EntityLog(String entityName, List<A> attributeSets) {
        this.entityName = entityName;
        for (int i = 0; i < attributeSets.size(); i++) {
            A attributeSet = attributeSets.get(i);
            // noinspection unchecked
            attributeSetLogList.add((L) attributeSet.getLog());
            attributeSetLogIndexList.put(attributeSet.name(), i);
        }
    }

    public String getEntityName() {
        return entityName;
    }

    public L get(int attributeSetIndex) {
        return attributeSetLogList.get(attributeSetIndex);
    }

    public L get(String attributeSetName) {
        return get(attributeSetLogIndexList.get(attributeSetName));
    }

    public int attributeSetLogCount() {
        return attributeSetLogList.size();
    }

    public void disconnectDatabases() {
        for (L attributeSetLog : attributeSetLogList)
            attributeSetLog.disconnectDatabase();

        attributeSetLogList.clear();
        attributeSetLogIndexList.clear();
    }
}
