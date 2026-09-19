package dev.modelarium.examples.schelling_segregation.entities.agents.attributes.location;

import dev.modelarium.examples.schelling_segregation.spatial.SchellingSpatialUtils;
import modelarium.entities.agentsets.ReadOnlyAgentSet;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.readonly.ReadOnlyAgent;

import java.util.List;
import java.util.function.Predicate;

/**
 * Relocates dissatisfied agents while preserving one-agent-per-cell occupancy under synchronous execution.
 *
 * <p>A dissatisfied agent first proposes a vacant destination. That proposal becomes visible to the rest of the
 * population at the next tick boundary. On the following tick, all contenders for a destination independently see
 * the same proposal set and therefore agree on the winner. This avoids two workers committing agents into the same
 * cell while retaining Modelarium's snapshot-at-tick-start interaction semantics.
 */
public final class RelocationRoutine extends AgentRoutine {
    private static final Predicate<ReadOnlyAgent> ALL_AGENTS = agent -> true;

    private final int width;
    private final int height;

    public RelocationRoutine(int width, int height) {
        super("relocate_if_dissatisfied", AttributeAccessLevel.PRIVATE);
        this.width = width;
        this.height = height;
    }

    @Override
    protected void run(AgentContext context) {
        LocationProperty locationProperty = (LocationProperty) context
                .getThisEntity()
                .getProperty("location", "location");
        MoveProposalProperty proposalProperty = (MoveProposalProperty) context
                .getThisEntity()
                .getProperty("location", "move_proposal");

        ReadOnlyAgentSet agents = context.getFilteredAgents(ALL_AGENTS);
        MoveProposal proposal = proposalProperty.get();

        if (proposal != null) {
            boolean targetStillVacant = SchellingSpatialUtils.vacantCells(agents, width, height)
                    .contains(proposal.target());

            if (targetStillVacant && proposalWins(context.getThisEntity().name(), proposal, agents)) {
                locationProperty.set(proposal.target());
                proposalProperty.set(null);
                return;
            }

            // The destination was taken, or another agent won this contested cell.
            proposalProperty.set(null);
        }

        boolean satisfied = (Boolean) context
                .getThisEntity()
                .getProperty("segregation", "satisfied")
                .get();

        if (satisfied)
            return;

        List<Cell> vacantCells = SchellingSpatialUtils.vacantCells(agents, width, height);
        if (vacantCells.isEmpty())
            return;

        Cell destination = vacantCells.get(context.getRandom().nextInt(vacantCells.size()));
        proposalProperty.set(new MoveProposal(destination, context.getRandom().nextLong()));
    }

    private static boolean proposalWins(String agentName, MoveProposal proposal, ReadOnlyAgentSet agents) {
        for (ReadOnlyAgent otherAgent : agents) {
            MoveProposal otherProposal = (MoveProposal) otherAgent
                    .getProperty("location", "move_proposal")
                    .get();

            if (otherProposal == null || !otherProposal.target().equals(proposal.target()))
                continue;

            if (otherProposal.priority() < proposal.priority())
                return false;

            if (otherProposal.priority() == proposal.priority()
                    && otherAgent.name().compareTo(agentName) < 0)
                return false;
        }

        return true;
    }
}
