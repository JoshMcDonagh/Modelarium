package dev.modelarium.examples.schelling_segregation.spatial;

import dev.modelarium.examples.schelling_segregation.entities.agents.attributes.demographics.Group;
import dev.modelarium.examples.schelling_segregation.entities.agents.attributes.location.Cell;
import modelarium.entities.agentsets.ReadOnlyAgentSet;
import modelarium.entities.readonly.ReadOnlyAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Shared spatial calculations used by the Schelling example and its summary reporting. */
public final class SchellingSpatialUtils {
    private SchellingSpatialUtils() {}

    public static Cell locationOf(ReadOnlyAgent agent) {
        return (Cell) agent.getProperty("location", "location").get();
    }

    public static Group groupOf(ReadOnlyAgent agent) {
        return (Group) agent.getProperty("demographics", "group").get();
    }

    public static Map<Cell, ReadOnlyAgent> occupancy(ReadOnlyAgentSet agents) {
        Map<Cell, ReadOnlyAgent> occupancy = new HashMap<>();
        for (ReadOnlyAgent agent : agents)
            occupancy.put(locationOf(agent), agent);
        return occupancy;
    }

    public static double similarNeighbourFraction(
            ReadOnlyAgent focalAgent,
            ReadOnlyAgentSet agents,
            int width,
            int height
    ) {
        return similarNeighbourFraction(
                groupOf(focalAgent),
                locationOf(focalAgent),
                occupancy(agents),
                width,
                height
        );
    }

    public static double similarNeighbourFraction(
            Group focalGroup,
            Cell focalLocation,
            Map<Cell, ReadOnlyAgent> occupancy,
            int width,
            int height
    ) {
        int occupiedNeighbours = 0;
        int similarNeighbours = 0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0)
                    continue;

                int x = focalLocation.x() + dx;
                int y = focalLocation.y() + dy;

                if (x < 0 || x >= width || y < 0 || y >= height)
                    continue;

                ReadOnlyAgent neighbour = occupancy.get(new Cell(x, y));
                if (neighbour == null)
                    continue;

                occupiedNeighbours++;
                if (groupOf(neighbour) == focalGroup)
                    similarNeighbours++;
            }
        }

        // With no occupied neighbours there is nobody dissimilar to the focal agent.
        if (occupiedNeighbours == 0)
            return 1.0;

        return (double) similarNeighbours / occupiedNeighbours;
    }

    public static List<Cell> vacantCells(ReadOnlyAgentSet agents, int width, int height) {
        Set<Cell> occupied = new HashSet<>();
        for (ReadOnlyAgent agent : agents)
            occupied.add(locationOf(agent));

        List<Cell> vacant = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Cell cell = new Cell(x, y);
                if (!occupied.contains(cell))
                    vacant.add(cell);
            }
        }
        return vacant;
    }
}
