package dev.modelarium.examples.axelrod_cultural_dissemination.config;

/**
 * Configuration for the Axelrod cultural dissemination replication example.
 *
 * @param modelSettings settings controlling the repeated experiment and per-run stopping safeguards
 * @param grid settings describing the fixed rectangular territory
 * @param culture settings describing the number of cultural features and alternative traits per feature
 */
public record AxelrodCulturalDisseminationSettings(
        ModelSettings modelSettings,
        Grid grid,
        Culture culture
) {
    /**
     * @param numOfReplications number of independent simulation runs in the replication experiment
     * @param maxNumOfEventsPerRun safety limit on the number of Axelrod activation events in any one run
     * @param stabilityCheckIntervalEvents number of activation events between absorbing-state checks
     * @param baseSeed seed used to generate the reproducible sequence of independent per-run seeds
     */
    public record ModelSettings(
            int numOfReplications,
            int maxNumOfEventsPerRun,
            int stabilityCheckIntervalEvents,
            long baseSeed
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
