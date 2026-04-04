package modelarium.entities.environments;

import modelarium.entities.Entity;
import modelarium.entities.attributes.AttributeSet;

import java.util.List;

public class Environment extends Entity {
    public Environment(String name, List<AttributeSet> attributeSets) {
        super(name, attributeSets);
    }

    @Override
    public Environment clone() {
        return getCloner().deepClone(this);
    }
}
