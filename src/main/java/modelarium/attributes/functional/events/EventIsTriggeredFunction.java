package modelarium.attributes.functional.events;

import modelarium.Entity;

@FunctionalInterface
public interface EventIsTriggeredFunction {
    boolean isTriggered(Entity owner);
}
