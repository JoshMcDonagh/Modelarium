package modelarium.entities.attributes.functional.properties;

import modelarium.entities.contexts.Context;

@FunctionalInterface
public interface PropertySetterFunction<T> {
    T set(Context context, T currentPropertyValue, T newValue);
}
