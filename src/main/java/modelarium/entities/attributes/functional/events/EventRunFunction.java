package modelarium.entities.attributes.functional.events;

import modelarium.entities.Entity;

@FunctionalInterface
public interface EventRunFunction {
    void run(Entity owner);
}
