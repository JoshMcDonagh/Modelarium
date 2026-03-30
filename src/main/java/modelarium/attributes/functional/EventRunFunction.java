package modelarium.attributes.functional;

import modelarium.Entity;

@FunctionalInterface
public interface EventRunFunction {
    void run(Entity associatedEntity);
}
