package modelarium.entities.attributes.functional.properties;

import modelarium.entities.Entity;

@FunctionalInterface
public interface PropertyGetterFunction<T> {
    T get(Entity owner, T propertyValue);
}
