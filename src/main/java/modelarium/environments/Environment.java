package modelarium.environments;

import modelarium.Entity;
import modelarium.attributes.AttributeSet;

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
