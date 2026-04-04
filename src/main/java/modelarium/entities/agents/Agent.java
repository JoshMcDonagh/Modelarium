package modelarium.entities.agents;

import modelarium.entities.Entity;
import modelarium.entities.attributes.AttributeSet;

import java.util.List;

public class Agent extends Entity {
    public Agent(String name, List<AttributeSet> attributeSets) {
        super(name, attributeSets);
    }

    @Override
    public Agent clone() {
        return getCloner().deepClone(this);
    }
}
