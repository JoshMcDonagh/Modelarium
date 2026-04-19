package modelarium.entities.attributes.events;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.contexts.EnvironmentContext;

public non-sealed abstract class EnvironmentEvent extends Event<EnvironmentContext> {
    public EnvironmentEvent(String name, boolean isLogged, AttributeAccessLevel accessLevel) {
        super(name, isLogged, accessLevel);
    }
}
