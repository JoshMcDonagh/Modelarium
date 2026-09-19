package dev.modelarium.examples.axelrod_cultural_dissemination.replication;

import dev.modelarium.examples.axelrod_cultural_dissemination.config.AxelrodCulturalDisseminationSettings;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/** Writes replication-level statistics alongside Modelarium's normal exported result files. */
public final class AxelrodReplicationExporter {
    private AxelrodReplicationExporter() {}

    public static void export(
            Path modelariumExportPath,
            AxelrodCulturalDisseminationSettings settings,
            List<AxelrodReplicationRunResult> runs,
            AxelrodReplicationSummary summary,
            int rawResultsRunNumber,
            long rawResultsRunSeed
    ) {
        Path replicationPath = modelariumExportPath.resolve("replication");

        try {
            Files.createDirectories(replicationPath);
            writeRunResults(replicationPath.resolve("run_results.csv"), runs);
            writeStableRegionDistribution(replicationPath.resolve("stable_region_distribution.csv"), runs);
            writeSummary(
                    replicationPath.resolve("summary_statistics.csv"),
                    settings,
                    summary,
                    rawResultsRunNumber,
                    rawResultsRunSeed
            );
            writePaperComparison(replicationPath.resolve("paper_comparison.csv"), summary);
            writeReadme(replicationPath.resolve("README.txt"), rawResultsRunNumber, rawResultsRunSeed);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export Axelrod replication statistics", e);
        }
    }

    private static void writeRunResults(Path path, List<AxelrodReplicationRunResult> runs) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(
                    "run,seed,stable_state_reached,events_processed,successful_interactions,"
                            + "final_cultural_region_count,largest_cultural_region_size,"
                            + "final_potential_interaction_pair_count,final_mean_neighbour_similarity"
            );
            writer.newLine();

            for (AxelrodReplicationRunResult run : runs) {
                writer.write(String.format(
                        Locale.ROOT,
                        "%d,%d,%s,%d,%d,%d,%d,%d,%.12f",
                        run.runNumber(),
                        run.seed(),
                        run.stableStateReached(),
                        run.eventsProcessed(),
                        run.successfulInteractions(),
                        run.finalCulturalRegionCount(),
                        run.largestCulturalRegionSize(),
                        run.finalPotentialInteractionPairCount(),
                        run.finalMeanNeighbourSimilarity()
                ));
                writer.newLine();
            }
        }
    }

    private static void writeStableRegionDistribution(
            Path path,
            List<AxelrodReplicationRunResult> runs
    ) throws IOException {
        List<AxelrodReplicationRunResult> stableRuns = runs.stream()
                .filter(AxelrodReplicationRunResult::stableStateReached)
                .toList();

        Map<Integer, Integer> distribution = new TreeMap<>();
        for (AxelrodReplicationRunResult run : stableRuns)
            distribution.merge(run.finalCulturalRegionCount(), 1, Integer::sum);

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("stable_region_count,run_count,percentage_of_stable_runs,percentage_of_all_runs");
            writer.newLine();

            for (Map.Entry<Integer, Integer> entry : distribution.entrySet()) {
                double stablePercentage = stableRuns.isEmpty()
                        ? Double.NaN
                        : 100.0 * entry.getValue() / stableRuns.size();
                double allRunsPercentage = 100.0 * entry.getValue() / runs.size();

                writer.write(String.format(
                        Locale.ROOT,
                        "%d,%d,%.6f,%.6f",
                        entry.getKey(),
                        entry.getValue(),
                        stablePercentage,
                        allRunsPercentage
                ));
                writer.newLine();
            }
        }
    }

    private static void writeSummary(
            Path path,
            AxelrodCulturalDisseminationSettings settings,
            AxelrodReplicationSummary summary,
            int rawResultsRunNumber,
            long rawResultsRunSeed
    ) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("metric,value");
            writer.newLine();

            writeMetric(writer, "grid_width", settings.grid().width());
            writeMetric(writer, "grid_height", settings.grid().height());
            writeMetric(writer, "population_size", settings.populationSize());
            writeMetric(writer, "cultural_features", settings.culture().numOfFeatures());
            writeMetric(writer, "traits_per_feature", settings.culture().traitsPerFeature());
            writeMetric(writer, "configured_replications", settings.modelSettings().numOfReplications());
            writeMetric(writer, "base_seed", settings.modelSettings().baseSeed());
            writeMetric(writer, "maximum_events_per_run", settings.modelSettings().maxNumOfEventsPerRun());
            writeMetric(writer, "stability_check_interval_events", settings.modelSettings().stabilityCheckIntervalEvents());
            writeMetric(writer, "stable_runs", summary.numOfStableRuns());
            writeMetric(writer, "runs_reaching_safety_limit", summary.numOfRunsAtSafetyLimit());
            writeMetric(writer, "mean_stable_region_count", summary.meanStableRegionCount());
            writeMetric(writer, "median_stable_region_count", summary.medianStableRegionCount());
            writeMetric(
                    writer,
                    "sample_standard_deviation_stable_region_count",
                    summary.sampleStandardDeviationStableRegionCount()
            );
            writeMetric(writer, "minimum_stable_region_count", summary.minimumStableRegionCount());
            writeMetric(writer, "maximum_stable_region_count", summary.maximumStableRegionCount());
            writeMetric(writer, "one_region_run_count", summary.oneRegionRunCount());
            writeMetric(writer, "one_region_run_percentage", summary.oneRegionRunPercentage());
            writeMetric(writer, "more_than_six_region_run_count", summary.moreThanSixRegionRunCount());
            writeMetric(writer, "more_than_six_region_run_percentage", summary.moreThanSixRegionRunPercentage());
            writeMetric(writer, "mean_events_to_detected_stability", summary.meanEventsToDetectedStability());
            writeMetric(writer, "median_events_to_detected_stability", summary.medianEventsToDetectedStability());
            writeMetric(
                    writer,
                    "mean_successful_interactions_to_detected_stability",
                    summary.meanSuccessfulInteractionsToDetectedStability()
            );
            writeMetric(writer, "mean_largest_stable_region_size", summary.meanLargestStableRegionSize());
            writeMetric(writer, "raw_modelarium_results_run_number", rawResultsRunNumber);
            writeMetric(writer, "raw_modelarium_results_run_seed", rawResultsRunSeed);
        }
    }

    private static void writePaperComparison(Path path, AxelrodReplicationSummary summary) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("metric,modelarium_value,axelrod_1997_value,axelrod_sample_size,notes");
            writer.newLine();

            writeComparison(
                    writer,
                    "median_stable_region_count",
                    summary.medianStableRegionCount(),
                    3.0,
                    100,
                    "Axelrod reports a median of three stable regions across 100 runs of the 10x10 F=5 q=10 setup."
            );
            writeComparison(
                    writer,
                    "one_region_run_percentage",
                    summary.oneRegionRunPercentage(),
                    14.0,
                    100,
                    "Axelrod reports one stable region in 14% of those 100 runs."
            );
            writeComparison(
                    writer,
                    "more_than_six_region_run_percentage",
                    summary.moreThanSixRegionRunPercentage(),
                    10.0,
                    100,
                    "Axelrod reports more than six stable regions in 10% of those 100 runs."
            );
            writeComparison(
                    writer,
                    "mean_stable_region_count",
                    summary.meanStableRegionCount(),
                    3.2,
                    10,
                    "The 3.2 mean is the F=5 q=10 cell of Axelrod's separate Table 2 experiment, which used 10 runs per condition."
            );
        }
    }

    private static void writeReadme(Path path, int rawResultsRunNumber, long rawResultsRunSeed) throws IOException {
        String text = """
                Axelrod cultural dissemination replication output
                ================================================

                run_results.csv
                  One row per independent seed/run, including stopping diagnostics and final cultural metrics.

                stable_region_distribution.csv
                  Frequency distribution of the number of stable cultural regions across runs that reached an
                  absorbing state before the safety limit.

                summary_statistics.csv
                  Experiment parameters and aggregate replication statistics.

                paper_comparison.csv
                  Direct comparison with the headline F=5, q=10 results reported by Robert Axelrod (1997),
                  The Dissemination of Culture: A Model with Local Convergence and Global Polarization.

                The normal Modelarium agent/environment files in the parent export directory are the raw results for
                replication run %d (seed %d). The CSV files in this replication directory summarize all runs.

                A run is classified as stable when no cardinal-neighbour pair has cultural similarity strictly between
                zero and one. The stability check is periodic, so events_processed may exceed the exact first absorbing
                event by less than one configured stability-check interval. This cannot alter the final state because an
                absorbing configuration cannot subsequently change.
                """.formatted(rawResultsRunNumber, rawResultsRunSeed);

        Files.writeString(path, text, StandardCharsets.UTF_8);
    }

    private static void writeMetric(BufferedWriter writer, String metric, Object value) throws IOException {
        writer.write(csv(metric));
        writer.write(',');
        writer.write(csv(value));
        writer.newLine();
    }

    private static void writeComparison(
            BufferedWriter writer,
            String metric,
            double modelariumValue,
            double paperValue,
            int paperSampleSize,
            String notes
    ) throws IOException {
        writer.write(csv(metric));
        writer.write(',');
        writer.write(csv(modelariumValue));
        writer.write(',');
        writer.write(csv(paperValue));
        writer.write(',');
        writer.write(csv(paperSampleSize));
        writer.write(',');
        writer.write(csv(notes));
        writer.newLine();
    }

    private static String csv(Object value) {
        if (value == null)
            return "";

        String text;
        if (value instanceof Double doubleValue)
            text = Double.isFinite(doubleValue) ? String.format(Locale.ROOT, "%.12f", doubleValue) : "";
        else
            text = value.toString();

        if (!text.contains(",") && !text.contains("\"") && !text.contains("\n") && !text.contains("\r"))
            return text;

        return '"' + text.replace("\"", "\"\"") + '"';
    }
}
