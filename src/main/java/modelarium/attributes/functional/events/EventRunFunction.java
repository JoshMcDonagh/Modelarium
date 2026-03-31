package modelarium.attributes.functional.events;

import modelarium.Entity;

@FunctionalInterface
public interface EventRunFunction {
    void run(Entity owner);
}
