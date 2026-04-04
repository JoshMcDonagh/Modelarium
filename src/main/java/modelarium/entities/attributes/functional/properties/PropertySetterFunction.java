package modelarium.entities.attributes.functional.properties;

import modelarium.entities.Entity;

@FunctionalInterface
public interface PropertySetterFunction<T> {
    T set(Entity owner, T currentPropertyValue, T newValue);
}
