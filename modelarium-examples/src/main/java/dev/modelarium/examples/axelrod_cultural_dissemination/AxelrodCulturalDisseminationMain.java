package dev.modelarium.examples.axelrod_cultural_dissemination;

import dev.modelarium.examples.axelrod_cultural_dissemination.config.AxelrodCulturalDisseminationSettings;
import dev.modelarium.examples.axelrod_cultural_dissemination.config.SettingsLoader;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.AxelrodAgentGenerator;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.environment.AxelrodEnvironmentGenerator;
import dev.modelarium.examples.axelrod_cultural_dissemination.replication.AxelrodReplicationExporter;
import dev.modelarium.examples.axelrod_cultural_dissemination.replication.AxelrodReplicationRunResult;
import dev.modelarium.examples.axelrod_cultural_dissemination.replication.AxelrodReplicationSummary;
import dev.modelarium.examples.axelrod_cultural_dissemination.scheduler.AxelrodEventScheduler;
import modelarium.Config;
import modelarium.Model;
import modelarium.results.readonly.ReadOnlyResults;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

/**
 * Modelarium replication experiment for Robert Axelrod's cultural dissemination model.
 *
 * <p>The default configuration reproduces the parameterisation for which Axelrod reported the distribution of
 * stable cultural regions across 100 independent runs: a 10x10 territory, five cultural features, ten traits per
 * feature, and cardinal-neighbour interaction. Every run receives a reproducible independent seed and proceeds until
 * an absorbing state is detected or a high safety limit is reached.
 */
public final class AxelrodCulturalDisseminationMain {
    private static final String CONFIG_RESOURCE =
            "dev/modelarium/examples/axelrod_cultural_dissemination/axelrod-cultural-dissemination-config.json";
    private static final String OUTPUT_DIRECTORY =
            "modelarium-examples/output/axelrod_cultural_dissemination";

    private AxelrodCulturalDisseminationMain() {}

    public static void main(String[] args) {
        AxelrodCulturalDisseminationSettings settings =
                SettingsLoader.loadAxelrodCulturalDisseminationConfig(CONFIG_RESOURCE);
        validateSettings(settings);

        SplittableRandom seedSequence = new SplittableRandom(settings.modelSettings().baseSeed());
        List<AxelrodReplicationRunResult> runResults = new ArrayList<>(
                settings.modelSettings().numOfReplications()
        );

        ReadOnlyResults rawResultsToExport = null;
        long rawResultsSeed = 0;
        int rawResultsRunNumber = 1;

        System.out.printf(
                "Running Axelrod cultural dissemination replication: %,d independent runs (%dx%d, F=%d, q=%d)%n",
                settings.modelSettings().numOfReplications(),
                settings.grid().width(),
                settings.grid().height(),
                settings.culture().numOfFeatures(),
                settings.culture().traitsPerFeature()
        );

        for (int runNumber = 1; runNumber <= settings.modelSettings().numOfReplications(); runNumber++) {
            long runSeed = seedSequence.nextLong();
            AxelrodEventScheduler scheduler = new AxelrodEventScheduler(
                    settings.grid().width(),
                    settings.grid().height(),
                    settings.modelSettings().maxNumOfEventsPerRun(),
                    settings.modelSettings().stabilityCheckIntervalEvents()
            );

            Model model = new Model(createConfig(settings, scheduler, runSeed));
            model.run();

            ReadOnlyResults results = model.getResults();
            AxelrodReplicationRunResult runResult = extractRunResult(
                    runNumber,
                    runSeed,
                    scheduler,
                    results
            );
            runResults.add(runResult);

            // Modelarium's normal export is retained for one concrete run. The replication-level CSV files added
            // below summarize every run, so exporting 100 complete per-agent result trees would add little value.
            if (runNumber == rawResultsRunNumber) {
                rawResultsToExport = results;
                rawResultsSeed = runSeed;
            }

            printRunProgress(runResult, settings.modelSettings().numOfReplications());
        }

        AxelrodReplicationSummary summary = AxelrodReplicationSummary.from(runResults);
        printExperimentSummary(summary);

        if (rawResultsToExport == null)
            throw new IllegalStateException("No Modelarium results were produced");

        Path outputPath = rawResultsToExport.export(OUTPUT_DIRECTORY);
        AxelrodReplicationExporter.export(
                outputPath,
                settings,
                runResults,
                summary,
                rawResultsRunNumber,
                rawResultsSeed
        );

        System.out.println("Results exported to: " + outputPath.toAbsolutePath());
    }

    private static Config createConfig(
            AxelrodCulturalDisseminationSettings settings,
            AxelrodEventScheduler scheduler,
            long seed
    ) {
        return Config
                .builder()
                .populationSize(settings.populationSize())
                // Tick 0 performs the complete sequential Axelrod process. Tick 1 is deliberately a no-op, allowing
                // the coordinator environment to observe/log the final worker state under Modelarium's synchronous
                // visibility semantics.
                .tickCount(2)
                .threadCount(1)
                .areThreadsSynced(true)
                .agentGenerator(new AxelrodAgentGenerator(settings))
                .environmentGenerator(new AxelrodEnvironmentGenerator(settings))
                .scheduler(scheduler)
                .seed(seed)
                .build();
    }

    private static AxelrodReplicationRunResult extractRunResult(
            int runNumber,
            long seed,
            AxelrodEventScheduler scheduler,
            ReadOnlyResults results
    ) {
        int regionCount = last(results.environment().attributeLogs(
                "cultural_metrics",
                "cultural_region_count",
                Integer.class
        ));
        int largestRegionSize = last(results.environment().attributeLogs(
                "cultural_metrics",
                "largest_cultural_region_size",
                Integer.class
        ));
        int potentialPairs = last(results.environment().attributeLogs(
                "cultural_metrics",
                "potential_interaction_pair_count",
                Integer.class
        ));
        double meanSimilarity = last(results.environment().attributeLogs(
                "cultural_metrics",
                "mean_neighbour_similarity",
                Double.class
        ));

        return new AxelrodReplicationRunResult(
                runNumber,
                seed,
                scheduler.stableStateReached(),
                scheduler.eventsProcessed(),
                scheduler.successfulInteractions(),
                regionCount,
                largestRegionSize,
                potentialPairs,
                meanSimilarity
        );
    }

    private static <T> T last(List<T> values) {
        if (values.isEmpty())
            throw new IllegalStateException("Expected a logged Axelrod environment metric");
        return values.getLast();
    }

    private static void printRunProgress(AxelrodReplicationRunResult run, int totalRuns) {
        String stoppingDescription = run.stableStateReached() ? "stable" : "SAFETY LIMIT";
        System.out.printf(
                "  Run %3d/%d: regions=%d, events=%,d, interactions=%,d, %s%n",
                run.runNumber(),
                totalRuns,
                run.finalCulturalRegionCount(),
                run.eventsProcessed(),
                run.successfulInteractions(),
                stoppingDescription
        );
    }

    private static void printExperimentSummary(AxelrodReplicationSummary summary) {
        System.out.println("Axelrod replication summary:");
        System.out.printf("  Runs reaching an absorbing state: %d/%d%n", summary.numOfStableRuns(), summary.numOfRuns());
        System.out.printf("  Mean stable regions: %.3f%n", summary.meanStableRegionCount());
        System.out.printf("  Median stable regions: %.3f%n", summary.medianStableRegionCount());
        System.out.printf("  Stable-region sample SD: %.3f%n", summary.sampleStandardDeviationStableRegionCount());
        System.out.printf(
                "  Range of stable regions: %d to %d%n",
                summary.minimumStableRegionCount(),
                summary.maximumStableRegionCount()
        );
        System.out.printf(
                "  One stable region: %d runs (%.1f%%; Axelrod: 14%% of 100 runs)%n",
                summary.oneRegionRunCount(),
                summary.oneRegionRunPercentage()
        );
        System.out.printf(
                "  More than six stable regions: %d runs (%.1f%%; Axelrod: 10%% of 100 runs)%n",
                summary.moreThanSixRegionRunCount(),
                summary.moreThanSixRegionRunPercentage()
        );
        System.out.printf(
                "  Axelrod's reported median for the same 100-run setup: 3; Modelarium: %.3f%n",
                summary.medianStableRegionCount()
        );
        System.out.printf(
                "  Secondary comparison: Axelrod Table 2 mean (10 runs) = 3.2; Modelarium mean = %.3f%n",
                summary.meanStableRegionCount()
        );
    }

    private static void validateSettings(AxelrodCulturalDisseminationSettings settings) {
        if (settings.grid().width() <= 0 || settings.grid().height() <= 0)
            throw new IllegalArgumentException("Grid width and height must be greater than 0");

        if (settings.culture().numOfFeatures() <= 0)
            throw new IllegalArgumentException("numOfFeatures must be greater than 0");

        if (settings.culture().traitsPerFeature() <= 1)
            throw new IllegalArgumentException("traitsPerFeature must be greater than 1");

        if (settings.modelSettings().numOfReplications() <= 0)
            throw new IllegalArgumentException("numOfReplications must be greater than 0");

        if (settings.modelSettings().maxNumOfEventsPerRun() <= 0)
            throw new IllegalArgumentException("maxNumOfEventsPerRun must be greater than 0");

        if (settings.modelSettings().stabilityCheckIntervalEvents() <= 0)
            throw new IllegalArgumentException("stabilityCheckIntervalEvents must be greater than 0");

        if (settings.modelSettings().stabilityCheckIntervalEvents()
                > settings.modelSettings().maxNumOfEventsPerRun())
            throw new IllegalArgumentException(
                    "stabilityCheckIntervalEvents must not exceed maxNumOfEventsPerRun"
            );
    }
}
