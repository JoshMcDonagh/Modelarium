package modelarium.entities.attributes.properties.functional;

import modelarium.entities.contexts.EnvironmentContext;

@FunctionalInterface
public interface EnvironmentPropertyRunFunction<T> {
    T run(EnvironmentContext context, T propertyValue);
}
