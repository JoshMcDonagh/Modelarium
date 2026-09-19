package dev.modelarium.examples.sir.entities.agents.attributes.location;

import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoordinatesTest {
    @Test
    void movementStaysWithinExclusiveAreaDimensions() {
        int width = 3;
        int height = 4;
        Coordinates coordinates = new Coordinates(width - 1, height - 1);
        SplittableRandom random = new SplittableRandom(1976L);

        for (int i = 0; i < 1_000; i++) {
            coordinates.moveRandomlyBy(random, 1, width, height);

            assertTrue(coordinates.getX() >= 0 && coordinates.getX() < width);
            assertTrue(coordinates.getY() >= 0 && coordinates.getY() < height);
        }
    }

    @Test
    void movementRejectsInvalidBounds() {
        Coordinates coordinates = new Coordinates(0, 0);
        SplittableRandom random = new SplittableRandom(1976L);

        assertThrows(IllegalArgumentException.class, () -> coordinates.moveRandomlyBy(random, 0, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> coordinates.moveRandomlyBy(random, 1, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> coordinates.moveRandomlyBy(random, 1, 1, 0));
    }
}
