package modelarium.entities.attributes.properties;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.EnvironmentAttribute;
import modelarium.entities.contexts.EnvironmentContext;

public non-sealed abstract class EnvironmentProperty<T> extends Property<T, EnvironmentContext> implements EnvironmentAttribute {
    public EnvironmentProperty(String name, boolean isLogged, AttributeAccessLevel accessLevel, Class<T> type) {
        super(name, isLogged, accessLevel, type);
    }
}
