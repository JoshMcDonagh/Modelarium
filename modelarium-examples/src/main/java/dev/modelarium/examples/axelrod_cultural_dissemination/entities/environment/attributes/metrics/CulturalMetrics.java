package dev.modelarium.examples.axelrod_cultural_dissemination.entities.environment.attributes.metrics;

import dev.modelarium.examples.axelrod_cultural_dissemination.spatial.AxelrodSpatialUtils;
import modelarium.entities.agentsets.ReadOnlyAgentSet;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.readonly.ReadOnlyAgent;

import java.util.function.Predicate;

/** Shared aggregate state used by the environment's logged Axelrod metrics. */
public final class CulturalMetrics {
    private static final Predicate<ReadOnlyAgent> ALL_AGENTS = agent -> true;

    private int regionCount = -1;
    private int largestRegionSize = -1;
    private int potentialInteractionPairCount = -1;
    private double meanNeighbourSimilarity = Double.NaN;
    private boolean initialized = false;

    void update(EnvironmentContext context, int width, int height) {
        ReadOnlyAgentSet agents = context.getFilteredAgents(ALL_AGENTS);

        regionCount = AxelrodSpatialUtils.culturalRegionCount(agents, width, height);
        largestRegionSize = AxelrodSpatialUtils.largestCulturalRegionSize(agents, width, height);
        potentialInteractionPairCount = AxelrodSpatialUtils.potentialInteractionPairCount(agents, width, height);
        meanNeighbourSimilarity = AxelrodSpatialUtils.meanNeighbourSimilarity(agents, width, height);
        initialized = true;
    }

    int regionCount() {
        return regionCount;
    }

    int largestRegionSize() {
        return largestRegionSize;
    }

    int potentialInteractionPairCount() {
        return potentialInteractionPairCount;
    }

    double meanNeighbourSimilarity() {
        return meanNeighbourSimilarity;
    }

    boolean initialized() {
        return initialized;
    }
}
