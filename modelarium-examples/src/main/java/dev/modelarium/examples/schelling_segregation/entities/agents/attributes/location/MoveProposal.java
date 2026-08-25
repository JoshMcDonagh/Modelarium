package dev.modelarium.examples.schelling_segregation.entities.agents.attributes.location;

import java.util.Objects;

/**
 * A proposed move to a vacant cell together with the random priority used to resolve competing proposals.
 *
 * <p>The fields are deliberately non-final so the value can be deep-cloned by Modelarium. No mutators are exposed,
 * so the object remains value-like to model code.
 */
public final class MoveProposal {
    private Cell target;
    private long priority;

    public MoveProposal(Cell target, long priority) {
        this.target = Objects.requireNonNull(target);
        this.priority = priority;
    }

    public Cell target() {
        return target;
    }

    public long priority() {
        return priority;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (!(object instanceof MoveProposal proposal))
            return false;
        return priority == proposal.priority && Objects.equals(target, proposal.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, priority);
    }

    @Override
    public String toString() {
        return "MoveProposal[target=" + target + ", priority=" + priority + "]";
    }
}
