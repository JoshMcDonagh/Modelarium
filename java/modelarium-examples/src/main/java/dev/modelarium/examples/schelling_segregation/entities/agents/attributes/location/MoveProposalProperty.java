package dev.modelarium.examples.schelling_segregation.entities.agents.attributes.location;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;

/** Stores an agent's pending relocation proposal, if it has one. */
public final class MoveProposalProperty extends AgentProperty<MoveProposal> {
    private MoveProposal proposal;

    public MoveProposalProperty() {
        super("move_proposal", false, AttributeAccessLevel.PUBLIC, MoveProposal.class);
    }

    @Override
    protected void set(AgentContext context, MoveProposal value) {
        proposal = value;
    }

    @Override
    protected MoveProposal get(AgentContext context) {
        return proposal;
    }
}
