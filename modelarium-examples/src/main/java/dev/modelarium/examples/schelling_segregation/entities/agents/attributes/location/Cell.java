package dev.modelarium.examples.schelling_segregation.entities.agents.attributes.location;

import java.util.Objects;

/**
 * Integer coordinate of one cell in the residential grid.
 *
 * <p>The fields are deliberately non-final. Modelarium deep-clones model state reflectively and Java records
 * prohibit reflective writes to their final component fields. The class remains value-like to callers because it
 * exposes no mutators.
 */
public final class Cell {
    private int x;
    private int y;

    public Cell(int x, int y) {
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
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (!(object instanceof Cell cell))
            return false;
        return x == cell.x && y == cell.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Cell[x=" + x + ", y=" + y + "]";
    }
}
