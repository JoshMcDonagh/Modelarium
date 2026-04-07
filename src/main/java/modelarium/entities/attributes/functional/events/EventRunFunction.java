package modelarium.entities.attributes.functional.events;

import modelarium.entities.contexts.Context;

@FunctionalInterface
public interface EventRunFunction {
    void run(Context context);
}
