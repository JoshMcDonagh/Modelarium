package dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.attributes.geography;

import java.util.Objects;

/** A fixed lattice position in the Axelrod territory. */
public final class GridPosition {
    // Deliberately non-final: Modelarium's reflective deep-cloner must be able to reconstruct model state.
    private int x;
    private int y;

    public GridPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof GridPosition position))
            return false;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
