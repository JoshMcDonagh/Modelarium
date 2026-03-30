package modelarium;

import modelarium.attributes.AttributeSet;
import utils.DeepCopyable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for all model elements in the simulation.
 *
 * <p>Each model element represents either an {@link modelarium.agents.Agent}
 * or the {@link modelarium.environments.Environment} and holds a unique name,
 * a set of attributes, and a reference to its {@link EntityAccessor}.
 */
public abstract class Entity implements DeepCopyable<Entity> {
    private final String name;
    private final List<AttributeSet> attributeSetList;
    private final Map<String, Integer> attributeSetIndexMap = new HashMap<String, Integer>();

    public Entity(String name, List<AttributeSet> attributeSetList) {
        this.name = name;
        this.attributeSetList = attributeSetList;
        for (int i = 0; i < this.attributeSetList.size(); i++) {
            AttributeSet attributeSet = this.attributeSetList.get(i);
            this.attributeSetIndexMap.put(attributeSet.name(), i);
        }
    }

    public String name() {
        return name;
    }


}
