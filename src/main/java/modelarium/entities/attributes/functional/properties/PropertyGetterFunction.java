package modelarium.entities.attributes.functional.properties;

import modelarium.entities.contexts.Context;

@FunctionalInterface
public interface PropertyGetterFunction<T> {
    T get(Context context, T propertyValue);
}
