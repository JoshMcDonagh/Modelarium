package modelarium.entities.attributes.events;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.contexts.AgentContext;

public non-sealed abstract class AgentEvent extends Event<AgentContext> {
    public AgentEvent(String name, boolean isLogged, AttributeAccessLevel accessLevel) {
        super(name, isLogged, accessLevel);
    }
}
