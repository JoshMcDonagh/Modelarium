package modelarium.entities.attributes.properties.functional;

import modelarium.entities.contexts.EnvironmentContext;

@FunctionalInterface
public interface EnvironmentPropertyGetterFunction<T> {
    T get(EnvironmentContext context, T propertyValue);
}
