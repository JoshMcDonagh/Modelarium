package modelarium.entities.attributes.functional.events;

import modelarium.entities.Entity;

@FunctionalInterface
public interface EventIsTriggeredFunction {
    boolean isTriggered(Entity owner);
}
