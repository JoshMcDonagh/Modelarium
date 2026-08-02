package dev.modelarium.examples.sirbasic.entities.agents.attributes.location;

import java.util.random.RandomGenerator;

public class Coordinates {
    private int x;
    private int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "x: " + x + " y: " + y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    void moveRandomlyBy(RandomGenerator random, int maxDistance, int maxX, int maxY) {
        if (maxDistance < 1)
            throw new IllegalArgumentException("maxDistance must be >= 1");

        int dx;
        int dy;

        do {
            dx = random.nextInt(-maxDistance, maxDistance + 1);
            dy = random.nextInt(-maxDistance, maxDistance + 1);
        } while (dx == 0 && dy == 0);

        x = Math.max(0, Math.min(x + dx, maxX));
        y = Math.max(0, Math.min(y + dy, maxY));
    }
}
