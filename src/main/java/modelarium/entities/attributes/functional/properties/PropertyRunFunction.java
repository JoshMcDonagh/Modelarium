package modelarium.entities.attributes.functional.properties;

import modelarium.entities.Entity;

@FunctionalInterface
public interface PropertyRunFunction<T> {
    T run(Entity owner, T propertyValue);
}
