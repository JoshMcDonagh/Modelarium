package dev.modelarium.examples.el_farol_bar.entities.agents.prediction;

import java.util.List;

/**
 * One focal attendance predictor held by an El Farol agent.
 *
 * <p>Each predictor is evaluated every week, whether or not the agent acted on it. Predictors therefore compete
 * according to how accurately they would have forecast the attendance history observed so far.
 */
public final class AttendancePredictor {
    enum Type {
        LAG,
        MIRROR,
        MOVING_AVERAGE,
        LINEAR_TREND,
        FIXED_PERCENTAGE
    }

    private final String name;
    private final Type type;
    private final int parameter;

    private long cumulativeAbsoluteError = 0;
    private int scoredPredictions = 0;
    private Integer pendingPrediction = null;

    AttendancePredictor(String name, Type type, int parameter) {
        this.name = name;
        this.type = type;
        this.parameter = parameter;
    }

    public String name() {
        return name;
    }

    /** Scores the prediction made for the newly realised week, if one exists. */
    public void scorePendingPrediction(int realisedAttendance) {
        if (pendingPrediction == null)
            return;

        cumulativeAbsoluteError += Math.abs((long) pendingPrediction - realisedAttendance);
        scoredPredictions++;
    }

    /** Makes and stores this predictor's forecast for the coming week. */
    public int predict(AttendanceHistory history, int populationSize) {
        int prediction = switch (type) {
            case LAG -> lagForecast(history);
            case MIRROR -> mirrorForecast(history, populationSize);
            case MOVING_AVERAGE -> movingAverageForecast(history);
            case LINEAR_TREND -> linearTrendForecast(history);
            case FIXED_PERCENTAGE -> fixedPercentageForecast(populationSize);
        };

        pendingPrediction = clamp(prediction, 0, populationSize);
        return pendingPrediction;
    }

    public int latestPrediction() {
        if (pendingPrediction == null)
            throw new IllegalStateException("Predictor has not yet made a prediction");
        return pendingPrediction;
    }

    /**
     * Error used to select the active predictor. All predictors in an agent's portfolio are scored every week, so
     * comparing cumulative absolute errors is equivalent to comparing their mean absolute errors.
     */
    public double errorScore() {
        if (scoredPredictions == 0)
            return Double.POSITIVE_INFINITY;

        return cumulativeAbsoluteError;
    }

    private int lagForecast(AttendanceHistory history) {
        return history.getFromEnd(Math.min(parameter, history.size()));
    }

    private int mirrorForecast(AttendanceHistory history, int populationSize) {
        int lag = history.getFromEnd(Math.min(parameter, history.size()));
        return populationSize - lag;
    }

    private int movingAverageForecast(AttendanceHistory history) {
        int window = Math.min(parameter, history.size());
        List<Integer> observations = history.last(window);
        double mean = observations.stream().mapToInt(Integer::intValue).average().orElseThrow();
        return (int) Math.round(mean);
    }

    /** Fits a least-squares line through the most recent observations and extrapolates it by one week. */
    private int linearTrendForecast(AttendanceHistory history) {
        int window = Math.min(parameter, history.size());
        List<Integer> observations = history.last(window);

        if (observations.size() == 1)
            return observations.getFirst();

        double meanX = (observations.size() - 1) / 2.0;
        double meanY = observations.stream().mapToInt(Integer::intValue).average().orElseThrow();

        double numerator = 0.0;
        double denominator = 0.0;
        for (int i = 0; i < observations.size(); i++) {
            double xDifference = i - meanX;
            numerator += xDifference * (observations.get(i) - meanY);
            denominator += xDifference * xDifference;
        }

        double slope = denominator == 0.0 ? 0.0 : numerator / denominator;
        double intercept = meanY - slope * meanX;
        double extrapolated = intercept + slope * observations.size();

        return (int) Math.round(extrapolated);
    }

    private int fixedPercentageForecast(int populationSize) {
        return (int) Math.round(populationSize * (parameter / 100.0));
    }

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(value, maximum));
    }
}
