package modelarium.attributes.functional;

import modelarium.Entity;

@FunctionalInterface
public interface EventIsTriggeredFunction {
    boolean isTriggered(Entity associatedEntity);
}
