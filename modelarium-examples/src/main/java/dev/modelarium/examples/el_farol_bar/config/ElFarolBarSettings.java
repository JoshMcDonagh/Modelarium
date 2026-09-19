package dev.modelarium.examples.el_farol_bar.config;

import java.util.List;

/**
 * Configuration values for the El Farol Bar example.
 *
 * @param modelSettings settings controlling execution of the model
 * @param bar settings describing the bar's crowding threshold
 * @param agents settings describing the population and agents' predictor portfolios
 * @param prediction settings used to initialise the public attendance history
 */
public record ElFarolBarSettings(
        ModelSettings modelSettings,
        Bar bar,
        Agents agents,
        Prediction prediction
) {
    public record ModelSettings(int numOfCores, int numOfWeeks, int summaryBurnInWeeks, long seed) {}

    public record Bar(int crowdingThreshold) {}

    public record Agents(int populationSize, int predictorsPerAgent) {}

    public record Prediction(List<Integer> initialAttendanceHistory) {}
}
