package modelarium.attributes.functional.properties;

import modelarium.Entity;

@FunctionalInterface
public interface PropertyGetterFunction<T> {
    T get(Entity owner, T propertyValue);
}
