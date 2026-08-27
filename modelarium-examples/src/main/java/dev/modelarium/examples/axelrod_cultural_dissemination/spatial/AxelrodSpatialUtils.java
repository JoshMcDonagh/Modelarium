package dev.modelarium.examples.axelrod_cultural_dissemination.spatial;

import dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.attributes.culture.Culture;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.attributes.geography.GridPosition;
import modelarium.entities.agentsets.ReadOnlyAgentSet;
import modelarium.entities.readonly.ReadOnlyAgent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Spatial and aggregate calculations for the Axelrod cultural dissemination example. */
public final class AxelrodSpatialUtils {
    private AxelrodSpatialUtils() {}

    public static GridPosition locationOf(ReadOnlyAgent agent) {
        return (GridPosition) agent.getProperty("geography", "location").get();
    }

    public static Culture cultureOf(ReadOnlyAgent agent) {
        return (Culture) agent.getProperty("culture", "culture").get();
    }

    /** Returns the cardinal (north/east/south/west) neighbours of the focal site without wrapping boundaries. */
    public static List<ReadOnlyAgent> neighbours(
            GridPosition focalPosition,
            ReadOnlyAgentSet agents,
            int width,
            int height
    ) {
        Map<GridPosition, ReadOnlyAgent> occupancy = occupancy(agents);
        List<ReadOnlyAgent> neighbours = new ArrayList<>(4);

        addIfOccupied(neighbours, occupancy, focalPosition.x() - 1, focalPosition.y(), width, height);
        addIfOccupied(neighbours, occupancy, focalPosition.x() + 1, focalPosition.y(), width, height);
        addIfOccupied(neighbours, occupancy, focalPosition.x(), focalPosition.y() - 1, width, height);
        addIfOccupied(neighbours, occupancy, focalPosition.x(), focalPosition.y() + 1, width, height);

        return neighbours;
    }

    /** Returns the number of contiguous cardinal-neighbour regions whose sites have identical cultures. */
    public static int culturalRegionCount(ReadOnlyAgentSet agents, int width, int height) {
        return regionSizes(culturesByPosition(agents), width, height).size();
    }

    /** Returns the number of sites in the largest contiguous identical-culture region. */
    public static int largestCulturalRegionSize(ReadOnlyAgentSet agents, int width, int height) {
        return regionSizes(culturesByPosition(agents), width, height).stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }

    /**
     * Returns the number of undirected neighbouring pairs that can still cause cultural change.
     *
     * <p>A pair is potentially active exactly when its similarity is greater than zero but less than one. A value
     * of zero therefore identifies an absorbing/stable Axelrod configuration.
     */
    public static int potentialInteractionPairCount(ReadOnlyAgentSet agents, int width, int height) {
        Map<GridPosition, Culture> cultures = culturesByPosition(agents);
        int potentialPairs = 0;

        for (Map.Entry<GridPosition, Culture> entry : cultures.entrySet()) {
            GridPosition position = entry.getKey();
            Culture culture = entry.getValue();

            Culture east = cultures.get(new GridPosition(position.x() + 1, position.y()));
            if (east != null && canChange(culture, east))
                potentialPairs++;

            Culture south = cultures.get(new GridPosition(position.x(), position.y() + 1));
            if (south != null && canChange(culture, south))
                potentialPairs++;
        }

        return potentialPairs;
    }

    /** Returns the mean cultural similarity across undirected cardinal-neighbour pairs. */
    public static double meanNeighbourSimilarity(ReadOnlyAgentSet agents, int width, int height) {
        Map<GridPosition, Culture> cultures = culturesByPosition(agents);
        double similaritySum = 0.0;
        int pairCount = 0;

        for (Map.Entry<GridPosition, Culture> entry : cultures.entrySet()) {
            GridPosition position = entry.getKey();
            Culture culture = entry.getValue();

            Culture east = cultures.get(new GridPosition(position.x() + 1, position.y()));
            if (east != null) {
                similaritySum += culture.similarity(east);
                pairCount++;
            }

            Culture south = cultures.get(new GridPosition(position.x(), position.y() + 1));
            if (south != null) {
                similaritySum += culture.similarity(south);
                pairCount++;
            }
        }

        return pairCount == 0 ? 1.0 : similaritySum / pairCount;
    }

    private static boolean canChange(Culture first, Culture second) {
        double similarity = first.similarity(second);
        return similarity > 0.0 && similarity < 1.0;
    }

    private static Map<GridPosition, ReadOnlyAgent> occupancy(ReadOnlyAgentSet agents) {
        Map<GridPosition, ReadOnlyAgent> occupancy = new HashMap<>();
        for (ReadOnlyAgent agent : agents)
            occupancy.put(locationOf(agent), agent);
        return occupancy;
    }

    private static Map<GridPosition, Culture> culturesByPosition(ReadOnlyAgentSet agents) {
        Map<GridPosition, Culture> cultures = new HashMap<>();
        for (ReadOnlyAgent agent : agents)
            cultures.put(locationOf(agent), cultureOf(agent));
        return cultures;
    }

    private static List<Integer> regionSizes(Map<GridPosition, Culture> cultures, int width, int height) {
        List<Integer> regionSizes = new ArrayList<>();
        Set<GridPosition> visited = new HashSet<>();

        for (Map.Entry<GridPosition, Culture> entry : cultures.entrySet()) {
            GridPosition start = entry.getKey();
            if (!visited.add(start))
                continue;

            Culture regionCulture = entry.getValue();
            int regionSize = 0;
            ArrayDeque<GridPosition> frontier = new ArrayDeque<>();
            frontier.add(start);

            while (!frontier.isEmpty()) {
                GridPosition current = frontier.removeFirst();
                regionSize++;

                visitSameCultureNeighbour(
                        current.x() - 1,
                        current.y(),
                        regionCulture,
                        cultures,
                        visited,
                        frontier,
                        width,
                        height
                );
                visitSameCultureNeighbour(
                        current.x() + 1,
                        current.y(),
                        regionCulture,
                        cultures,
                        visited,
                        frontier,
                        width,
                        height
                );
                visitSameCultureNeighbour(
                        current.x(),
                        current.y() - 1,
                        regionCulture,
                        cultures,
                        visited,
                        frontier,
                        width,
                        height
                );
                visitSameCultureNeighbour(
                        current.x(),
                        current.y() + 1,
                        regionCulture,
                        cultures,
                        visited,
                        frontier,
                        width,
                        height
                );
            }

            regionSizes.add(regionSize);
        }

        return regionSizes;
    }

    private static void visitSameCultureNeighbour(
            int x,
            int y,
            Culture regionCulture,
            Map<GridPosition, Culture> cultures,
            Set<GridPosition> visited,
            ArrayDeque<GridPosition> frontier,
            int width,
            int height
    ) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return;

        GridPosition position = new GridPosition(x, y);
        Culture culture = cultures.get(position);
        if (culture == null || !culture.equals(regionCulture) || !visited.add(position))
            return;

        frontier.addLast(position);
    }

    private static void addIfOccupied(
            List<ReadOnlyAgent> neighbours,
            Map<GridPosition, ReadOnlyAgent> occupancy,
            int x,
            int y,
            int width,
            int height
    ) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return;

        ReadOnlyAgent agent = occupancy.get(new GridPosition(x, y));
        if (agent != null)
            neighbours.add(agent);
    }
}
