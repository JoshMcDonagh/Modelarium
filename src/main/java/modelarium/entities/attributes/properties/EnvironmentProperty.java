package modelarium.entities.attributes.properties;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;

public non-sealed abstract class EnvironmentProperty<T> extends Property<T, EnvironmentContext> {
    public EnvironmentProperty(String name, boolean isLogged, AttributeAccessLevel accessLevel, Class<T> type) {
        super(name, isLogged, accessLevel, type);
    }
}
