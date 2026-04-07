package modelarium.entities.attributes.functional.events;

import modelarium.entities.contexts.Context;

@FunctionalInterface
public interface EventIsTriggeredFunction {
    boolean isTriggered(Context context);
}
