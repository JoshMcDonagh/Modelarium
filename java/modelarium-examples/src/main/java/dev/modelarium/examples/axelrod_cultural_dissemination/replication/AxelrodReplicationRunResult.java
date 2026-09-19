package dev.modelarium.examples.axelrod_cultural_dissemination.replication;

/** Final outcome and stopping diagnostics for one independent Axelrod replication run. */
public record AxelrodReplicationRunResult(
        int runNumber,
        long seed,
        boolean stableStateReached,
        long eventsProcessed,
        long successfulInteractions,
        int finalCulturalRegionCount,
        int largestCulturalRegionSize,
        int finalPotentialInteractionPairCount,
        double finalMeanNeighbourSimilarity
) {}
