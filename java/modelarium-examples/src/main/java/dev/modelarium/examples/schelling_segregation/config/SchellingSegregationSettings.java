package dev.modelarium.examples.schelling_segregation.config;

/**
 * Configuration values for the Schelling segregation example.
 *
 * @param modelSettings settings controlling execution of the model
 * @param grid settings describing the rectangular residential grid
 * @param population settings describing occupancy and the relative sizes of the two groups
 * @param segregation settings controlling agents' satisfaction rule
 */
public record SchellingSegregationSettings(
        ModelSettings modelSettings,
        Grid grid,
        Population population,
        Segregation segregation
) {
    public record ModelSettings(int numOfCores, int numOfTicks, long seed) {}

    public record Grid(int width, int height) {}

    public record Population(double vacancyProportion, double groupOneProportion) {}

    public record Segregation(double minimumSimilarNeighbourFraction) {}

    /** Returns the number of cells in the residential grid. */
    public int cellCount() {
        return grid.width() * grid.height();
    }

    /** Returns the number of agents implied by the configured vacancy proportion. */
    public int populationSize() {
        return (int) Math.round(cellCount() * (1.0 - population.vacancyProportion()));
    }
}
