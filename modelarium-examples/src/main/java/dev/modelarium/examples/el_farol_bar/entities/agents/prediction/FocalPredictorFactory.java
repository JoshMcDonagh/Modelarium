package dev.modelarium.examples.el_farol_bar.entities.agents.prediction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Creates a diverse "alphabet soup" of simple focal predictors and gives each El Farol agent a fixed random
 * portfolio. Arthur (1994) deliberately leaves the exact predictor bank under-specified, so the concrete bank here
 * is an explicit implementation choice built from the predictor forms he describes, plus fixed forecasts commonly
 * used in later computational replications.
 */
public final class FocalPredictorFactory {
    private record Specification(String name, AttendancePredictor.Type type, int parameter) {
        AttendancePredictor createPredictor() {
            return new AttendancePredictor(name, type, parameter);
        }

        int fixedForecast(int populationSize) {
            if (type != AttendancePredictor.Type.FIXED_PERCENTAGE)
                throw new IllegalStateException("Not a fixed predictor");

            return (int) Math.round(populationSize * (parameter / 100.0));
        }
    }

    private static final List<Specification> PREDICTOR_BANK = createPredictorBank();

    private FocalPredictorFactory() {}

    /**
     * Gives an agent a random portfolio of distinct predictor specifications.
     *
     * <p>The portfolio is seeded with one fixed forecast on each side of the crowding threshold. This mirrors the
     * diversity condition used in descriptions of Arthur's computational experiment and avoids giving an agent a
     * portfolio that can never contemplate one of the two choices.
     */
    public static List<AttendancePredictor> createPortfolio(
            int predictorCount,
            int populationSize,
            int crowdingThreshold,
            RandomGenerator random
    ) {
        if (predictorCount < 2)
            throw new IllegalArgumentException("predictorCount must be at least 2");

        if (predictorCount > PREDICTOR_BANK.size())
            throw new IllegalArgumentException(
                    "predictorCount cannot exceed predictor bank size of " + PREDICTOR_BANK.size()
            );

        List<Specification> atOrBelowThreshold = PREDICTOR_BANK.stream()
                .filter(specification -> specification.type() == AttendancePredictor.Type.FIXED_PERCENTAGE)
                .filter(specification -> specification.fixedForecast(populationSize) <= crowdingThreshold)
                .toList();

        List<Specification> aboveThreshold = PREDICTOR_BANK.stream()
                .filter(specification -> specification.type() == AttendancePredictor.Type.FIXED_PERCENTAGE)
                .filter(specification -> specification.fixedForecast(populationSize) > crowdingThreshold)
                .toList();

        if (atOrBelowThreshold.isEmpty() || aboveThreshold.isEmpty())
            throw new IllegalArgumentException(
                    "crowdingThreshold must leave possible attendance values on both sides of the threshold"
            );

        Specification lowerAnchor = atOrBelowThreshold.get(random.nextInt(atOrBelowThreshold.size()));
        Specification upperAnchor = aboveThreshold.get(random.nextInt(aboveThreshold.size()));

        List<Specification> remainingBank = new ArrayList<>(PREDICTOR_BANK);
        remainingBank.remove(lowerAnchor);
        remainingBank.remove(upperAnchor);
        Collections.shuffle(remainingBank, random);

        List<AttendancePredictor> predictors = new ArrayList<>();
        predictors.add(lowerAnchor.createPredictor());
        predictors.add(upperAnchor.createPredictor());

        for (int i = 0; predictors.size() < predictorCount; i++)
            predictors.add(remainingBank.get(i).createPredictor());

        return predictors;
    }

    public static int bankSize() {
        return PREDICTOR_BANK.size();
    }

    private static List<Specification> createPredictorBank() {
        List<Specification> bank = new ArrayList<>();

        // Cycle/lag predictors, including Arthur's examples "same as last week", "same as 2 weeks ago" and
        // "same as 5 weeks ago". Twenty weeks is also a common maximum memory in later implementations.
        for (int lag = 1; lag <= 20; lag++)
            bank.add(new Specification("lag_" + lag, AttendancePredictor.Type.LAG, lag));

        // Mirror image around half the population, applied to different historical lags.
        for (int lag = 1; lag <= 20; lag++)
            bank.add(new Specification("mirror_lag_" + lag, AttendancePredictor.Type.MIRROR, lag));

        // Rounded recent averages, including Arthur's explicit average-of-the-last-four example.
        for (int window = 2; window <= 20; window++)
            bank.add(new Specification("average_" + window, AttendancePredictor.Type.MOVING_AVERAGE, window));

        // Bounded linear extrapolations, including Arthur's explicit trend-over-the-last-eight example.
        for (int window = 3; window <= 20; window++)
            bank.add(new Specification("trend_" + window, AttendancePredictor.Type.LINEAR_TREND, window));

        // Fixed forecasts increase portfolio diversity and are used by well-known computational replications of
        // the El Farol model. Zero is included so the configurable example remains valid for low thresholds.
        for (int percentage = 0; percentage <= 100; percentage += 5)
            bank.add(new Specification(
                    "fixed_" + percentage + "pct",
                    AttendancePredictor.Type.FIXED_PERCENTAGE,
                    percentage
            ));

        return List.copyOf(bank);
    }
}
