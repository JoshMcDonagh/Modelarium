package modelarium.entities.contexts;

import modelarium.entities.agents.Agent;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.immutable.ImmutableEnvironment;

public sealed interface AgentContext extends EntityContext permits AgentSimulationContext {
    Agent getThisEntity();
    AgentAttributeSet getThisAttributeSet();
    Attribute<AgentSimulationContext> getThisAttribute();
    ImmutableEnvironment getEnvironment();
}
