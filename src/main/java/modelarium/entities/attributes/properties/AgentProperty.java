package modelarium.entities.attributes.properties;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.contexts.AgentContext;

public non-sealed abstract class AgentProperty<T> extends Property<T, AgentContext> {
    public AgentProperty(String name, boolean isLogged, AttributeAccessLevel accessLevel, Class<T> type) {
        super(name, isLogged, accessLevel, type);
    }
}
