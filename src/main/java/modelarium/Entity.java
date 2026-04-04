package modelarium;

import com.rits.cloning.Cloner;
import modelarium.attributes.*;
import modelarium.attributes.Routine;
import modelarium.attributes.AttributeSet;
import modelarium.contexts.AgentContext;
import modelarium.contexts.Context;
import modelarium.logging.EntityLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Entity {
    private static final Cloner cloner = new Cloner();

    protected static Cloner getCloner() {
        return cloner;
    }

    private final String name;
    private final List<AttributeSet> attributeSetList;
    private final Map<String, Integer> attributeSetIndexMap = new HashMap<>();

    public Entity(String name, List<AttributeSet> attributeSetList) {
        this.name = name;
        this.attributeSetList = attributeSetList;
        for (int i = 0; i < this.attributeSetList.size(); i++) {
            AttributeSet attributeSet = this.attributeSetList.get(i);
            this.attributeSetIndexMap.put(attributeSet.name(), i);
        }
    }

    public void setContext(Context context) {
        for (AttributeSet attributeSet : attributeSetList)
            attributeSet.setContext(context);
    }

    public String name() {
        return name;
    }

    public int attributeSetCount() {
        return attributeSetList.size();
    }

    public int attributeCount() {
        int count = 0;

        for (AttributeSet attributeSet : attributeSetList)
            count += attributeSet.size();

        return count;
    }

    public AttributeSet getAttributeSet(int attributeSetIndex) {
        return attributeSetList.get(attributeSetIndex);
    }

    public AttributeSet getAttributeSet(String attributeSetName) {
        return getAttributeSet(attributeSetIndexMap.get(attributeSetName));
    }

    public Attribute getAttribute(String attributeSetName, String attributeName) {
        return getAttributeSet(attributeSetName).get(attributeName);
    }

    public Event getEvent(String attributeSetName, String eventName) {
        return getAttributeSet(attributeSetName).getEvent(eventName);
    }

    public Routine getRoutine(String attributeSetName, String routineName) {
        return getAttributeSet(attributeSetName).getRoutine(routineName);
    }

    public Property<?> getProperty(String attributeSetName, String propertyName) {
        return getAttributeSet(attributeSetName).getProperty(propertyName);
    }

    public EntityLog getLog() {
        return new EntityLog(name, attributeSetList);
    }

    public void run() {
        for (AttributeSet attributeSet : attributeSetList)
            attributeSet.run();
    }

    public abstract Entity clone();
}
