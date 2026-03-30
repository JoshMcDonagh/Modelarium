package modelarium.attributes.functional;

import modelarium.Entity;

@FunctionalInterface
public interface PropertyGetterFunction<T> {
    T get(Entity associatedEntity, T propertyValue);
}
