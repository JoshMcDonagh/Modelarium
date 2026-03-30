package modelarium.attributes.functional;

import modelarium.Entity;

@FunctionalInterface
public interface PropertySetterFunction<T> {
    T set(Entity associatedEntity, T currentPropertyValue, T newValue);
}
