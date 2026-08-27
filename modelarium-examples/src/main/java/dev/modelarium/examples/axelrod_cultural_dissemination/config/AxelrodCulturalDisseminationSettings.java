package dev.modelarium.examples.axelrod_cultural_dissemination.config;

/**
 * Configuration for the Axelrod cultural dissemination example.
 *
 * @param modelSettings settings controlling the number of social-influence events, batching, random seed, and metric sampling
 * @param grid settings describing the fixed rectangular territory
 * @param culture settings describing the number of cultural features and alternative traits per feature
 */
public record AxelrodCulturalDisseminationSettings(
        ModelSettings modelSettings,
        Grid grid,
        Culture culture
) {
    /**
     * @param numOfEvents number of Axelrod activation events to simulate
     * @param eventsPerModelariumTick number of sequential Axelrod events processed between Modelarium barriers
     * @param metricMeasurementIntervalEvents number of Axelrod events between aggregate metric recalculations
     * @param seed random seed used by Modelarium
     */
    public record ModelSettings(
            int numOfEvents,
            int eventsPerModelariumTick,
            int metricMeasurementIntervalEvents,
            long seed
    ) {}

    /** Fixed rectangular lattice dimensions. */
    public record Grid(int width, int height) {}

    /** Axelrod's F cultural features and q possible traits per feature. */
    public record Culture(int numOfFeatures, int traitsPerFeature) {}

    /** Returns the number of fixed sites/agents in the territory. */
    public int populationSize() {
        return grid.width() * grid.height();
    }
}
