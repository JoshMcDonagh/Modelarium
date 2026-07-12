package modelarium.entities.contexts;

import modelarium.entities.agents.Agent;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.AttributeBase;
import modelarium.entities.immutable.ImmutableEnvironment;

public sealed interface AgentContext extends EntityContext permits AgentSimulationContext {
    Agent getThisEntity();
    AgentAttributeSet getThisAttributeSet();
    AttributeBase<AgentSimulationContext> getThisAttribute();
    ImmutableEnvironment getEnvironment();
}
