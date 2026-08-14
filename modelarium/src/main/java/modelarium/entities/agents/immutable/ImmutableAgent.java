package modelarium.entities.agents.immutable;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.ImmutableEntity;
import modelarium.entities.attributes.sets.immutable.ImmutableAgentAttributeSet;
import modelarium.entities.logging.AttributeSetLog;

/**
 * Class for providing a read-only view of an {@link MutableAgent}.
 *
 * <p>This class wraps a mutable agent so that other model elements can inspect it without being able to modify it.
 */
public final class ImmutableAgent extends ImmutableEntity<AgentSimulationContext, AgentContext, MutableAgentAttributeSet, AttributeSetLog<AgentSimulationContext>> implements Agent {

    /**
     * Constructs a new immutable agent wrapping the specified mutable agent.
     *
     * @param entity the mutable agent to provide a read-only view of
     */
    public ImmutableAgent(MutableAgent entity) {
        super(entity);
    }

    /**
     * Retrieves a read-only view of one of the wrapped agent's attribute sets by index.
     *
     * @param attributeSetIndex the index of the attribute set to retrieve
     * @return a new {@link ImmutableAgentAttributeSet} instance wrapping the attribute set at the specified index
     */
    @Override
    public ImmutableAgentAttributeSet getAttributeSet(int attributeSetIndex) {
        return new ImmutableAgentAttributeSet(getMutableEntity().getAttributeSet(attributeSetIndex));
    }

    /**
     * Retrieves a read-only view of one of the wrapped agent's attribute sets by name.
     *
     * @param attributeSetName the name of the attribute set to retrieve
     * @return a new {@link ImmutableAgentAttributeSet} instance wrapping the attribute set with the specified name
     */
    @Override
    public ImmutableAgentAttributeSet getAttributeSet(String attributeSetName) {
        return new ImmutableAgentAttributeSet(getMutableEntity().getAttributeSet(attributeSetName));
    }

    @Override
    public boolean isDead() {
        return ((MutableAgent) getMutableEntity()).isDead();
    }
}
