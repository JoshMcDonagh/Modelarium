package modelarium.entities.immutable;

import modelarium.entities.agents.Agent;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.immutable.attributes.ImmutableAgentAttributeSet;
import modelarium.entities.logging.AttributeSetLog;

public final class ImmutableAgent extends ImmutableEntity<AgentSimulationContext, AgentContext, AgentAttributeSet, AttributeSetLog<AgentSimulationContext>> {
    public ImmutableAgent(Agent entity) {
        super(entity);
    }

    @Override
    public ImmutableAgentAttributeSet getAttributeSet(int attributeSetIndex) {
        return new ImmutableAgentAttributeSet(getMutableEntity().getAttributeSet(attributeSetIndex));
    }

    @Override
    public ImmutableAgentAttributeSet getAttributeSet(String attributeSetName) {
        return new ImmutableAgentAttributeSet(getMutableEntity().getAttributeSet(attributeSetName));
    }
}
