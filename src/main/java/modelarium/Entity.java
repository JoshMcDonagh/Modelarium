package modelarium;

import com.rits.cloning.Cloner;
import modelarium.attributes.*;
import modelarium.attributes.Routine;
import modelarium.attributes.AttributeSet;
import modelarium.logging.AttributeSetLog;
import modelarium.logging.EntityLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for all model elements in the simulation.
 *
 * <p>Each model element represents either an {@link modelarium.agents.Agent}
 * or the {@link modelarium.environments.Environment} and holds a unique name,
 * a set of attributes, and a reference to its {@link AccessibleContext}.
 */
public abstract class Entity {
    private static final Cloner cloner = new Cloner();

    protected static Cloner getCloner() {
        return cloner;
    }

    private final String name;
    private final List<AttributeSet> attributeSetList;
    private final Map<String, Integer> attributeSetIndexMap = new HashMap<>();

    private AccessibleContext context = null;

    public Entity(String name, List<AttributeSet> attributeSetList) {
        this.name = name;
        this.attributeSetList = attributeSetList;
        for (int i = 0; i < this.attributeSetList.size(); i++) {
            AttributeSet attributeSet = this.attributeSetList.get(i);
            this.attributeSetIndexMap.put(attributeSet.name(), i);
        }
    }

    public void setContext(AccessibleContext context) {
        this.context = context;
    }

    public AccessibleContext accessibleContext() {
        return context;
    }

    public String name() {
        return name;
    }

    public int numOfAttributeSets() {
        return attributeSetList.size();
    }

    public int numOfAttributes() {
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
        return new EntityLog(attributeSetList);
    }

    public void run() {
        for (AttributeSet attributeSet : attributeSetList)
            attributeSet.run();
    }

    public abstract Entity clone();
}
