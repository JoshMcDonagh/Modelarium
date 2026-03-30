package modelarium.attributes.functional;

import modelarium.Entity;

@FunctionalInterface
public interface PropertyRunFunction<T> {
    T run(Entity associatedEntity, T propertyValue);
}
