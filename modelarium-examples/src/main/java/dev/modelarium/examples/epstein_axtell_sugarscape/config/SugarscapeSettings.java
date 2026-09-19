package dev.modelarium.examples.epstein_axtell_sugarscape.config;

/** Configuration for the Chapter II Sugarscape replication experiment suite. */
public record SugarscapeSettings(
        long baseSeed,
        int immediateGrowbackTicks,
        int selectionTicks,
        int carryingCapacityTicks,
        int carryingCapacityReplications,
        int wealthTicks,
        int neighbourNetworkTicks,
        int waveTicks,
        int seasonalTicks,
        int pollutionTicks
) {}
