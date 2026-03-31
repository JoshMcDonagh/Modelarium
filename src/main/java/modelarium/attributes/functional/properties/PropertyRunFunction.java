package modelarium.attributes.functional.properties;

import modelarium.Entity;

@FunctionalInterface
public interface PropertyRunFunction<T> {
    T run(Entity owner, T propertyValue);
}
