package modelarium.entities.attributes.properties.functional;

import modelarium.entities.contexts.EnvironmentContext;

public interface EnvironmentPropertySetterFunction<T> {
    T set(EnvironmentContext context, T currentPropertyValue, T newPropertyValue);
}
