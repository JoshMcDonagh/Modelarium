package modelarium.entities.attributes.functional.properties;

import modelarium.entities.contexts.Context;

@FunctionalInterface
public interface PropertyRunFunction<T> {
    T run(Context context, T propertyValue);
}
