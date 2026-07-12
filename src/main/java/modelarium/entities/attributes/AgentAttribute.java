package modelarium.entities.attributes;

import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;

public sealed interface AgentAttribute extends Attribute permits AgentEvent, AgentProperty, AgentRoutine {
}
