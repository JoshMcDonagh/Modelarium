package modelarium.attributes.functional.properties;

import modelarium.Entity;

@FunctionalInterface
public interface PropertySetterFunction<T> {
    T set(Entity owner, T currentPropertyValue, T newValue);
}
