package dev.modelarium.examples.axelrod_cultural_dissemination.replication;

import java.util.List;

/** Aggregate statistics across the independent runs in an Axelrod replication experiment. */
public record AxelrodReplicationSummary(
        int numOfRuns,
        int numOfStableRuns,
        int numOfRunsAtSafetyLimit,
        double meanStableRegionCount,
        double medianStableRegionCount,
        double sampleStandardDeviationStableRegionCount,
        int minimumStableRegionCount,
        int maximumStableRegionCount,
        int oneRegionRunCount,
        double oneRegionRunPercentage,
        int moreThanSixRegionRunCount,
        double moreThanSixRegionRunPercentage,
        double meanEventsToDetectedStability,
        double medianEventsToDetectedStability,
        double meanSuccessfulInteractionsToDetectedStability,
        double meanLargestStableRegionSize
) {
    public static AxelrodReplicationSummary from(List<AxelrodReplicationRunResult> runs) {
        if (runs == null || runs.isEmpty())
            throw new IllegalArgumentException("At least one replication run is required");

        List<AxelrodReplicationRunResult> stableRuns = runs.stream()
                .filter(AxelrodReplicationRunResult::stableStateReached)
                .toList();

        if (stableRuns.isEmpty()) {
            return new AxelrodReplicationSummary(
                    runs.size(),
                    0,
                    runs.size(),
                    Double.NaN,
                    Double.NaN,
                    Double.NaN,
                    0,
                    0,
                    0,
                    Double.NaN,
                    0,
                    Double.NaN,
                    Double.NaN,
                    Double.NaN,
                    Double.NaN,
                    Double.NaN
            );
        }

        List<Integer> regionCounts = stableRuns.stream()
                .map(AxelrodReplicationRunResult::finalCulturalRegionCount)
                .sorted()
                .toList();
        List<Long> eventCounts = stableRuns.stream()
                .map(AxelrodReplicationRunResult::eventsProcessed)
                .sorted()
                .toList();

        double meanRegions = regionCounts.stream().mapToInt(Integer::intValue).average().orElseThrow();
        double sampleSd = sampleStandardDeviation(regionCounts, meanRegions);
        int oneRegionRuns = (int) stableRuns.stream()
                .filter(run -> run.finalCulturalRegionCount() == 1)
                .count();
        int moreThanSixRegionRuns = (int) stableRuns.stream()
                .filter(run -> run.finalCulturalRegionCount() > 6)
                .count();

        return new AxelrodReplicationSummary(
                runs.size(),
                stableRuns.size(),
                runs.size() - stableRuns.size(),
                meanRegions,
                medianIntegers(regionCounts),
                sampleSd,
                regionCounts.getFirst(),
                regionCounts.getLast(),
                oneRegionRuns,
                percentage(oneRegionRuns, stableRuns.size()),
                moreThanSixRegionRuns,
                percentage(moreThanSixRegionRuns, stableRuns.size()),
                stableRuns.stream().mapToLong(AxelrodReplicationRunResult::eventsProcessed).average().orElseThrow(),
                medianLongs(eventCounts),
                stableRuns.stream().mapToLong(AxelrodReplicationRunResult::successfulInteractions).average().orElseThrow(),
                stableRuns.stream().mapToInt(AxelrodReplicationRunResult::largestCulturalRegionSize).average().orElseThrow()
        );
    }

    private static double sampleStandardDeviation(List<Integer> values, double mean) {
        if (values.size() < 2)
            return 0.0;

        double sumSquaredDeviations = 0.0;
        for (int value : values) {
            double deviation = value - mean;
            sumSquaredDeviations += deviation * deviation;
        }
        return Math.sqrt(sumSquaredDeviations / (values.size() - 1));
    }

    private static double medianIntegers(List<Integer> sortedValues) {
        int middle = sortedValues.size() / 2;
        if (sortedValues.size() % 2 == 1)
            return sortedValues.get(middle);
        return (sortedValues.get(middle - 1) + sortedValues.get(middle)) / 2.0;
    }

    private static double medianLongs(List<Long> sortedValues) {
        int middle = sortedValues.size() / 2;
        if (sortedValues.size() % 2 == 1)
            return sortedValues.get(middle);
        return (sortedValues.get(middle - 1) + sortedValues.get(middle)) / 2.0;
    }

    private static double percentage(int count, int total) {
        return 100.0 * count / total;
    }
}
