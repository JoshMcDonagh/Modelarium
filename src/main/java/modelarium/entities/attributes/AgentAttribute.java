package modelarium.entities.attributes;

import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;

/**
 * Interface for marking an attribute as belonging to an agent.
 *
 * <p>This interface is implemented by the agent-flavoured attribute types: {@link AgentEvent}, {@link AgentProperty}
 * and {@link AgentRoutine}.
 */
public sealed interface AgentAttribute extends Attribute permits AgentEvent, AgentProperty, AgentRoutine {
}
