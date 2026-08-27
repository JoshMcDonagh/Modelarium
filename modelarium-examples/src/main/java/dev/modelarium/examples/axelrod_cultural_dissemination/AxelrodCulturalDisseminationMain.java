package dev.modelarium.examples.axelrod_cultural_dissemination;

import dev.modelarium.examples.axelrod_cultural_dissemination.config.AxelrodCulturalDisseminationSettings;
import dev.modelarium.examples.axelrod_cultural_dissemination.config.SettingsLoader;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.AxelrodAgentGenerator;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.environment.AxelrodEnvironmentGenerator;
import dev.modelarium.examples.axelrod_cultural_dissemination.scheduler.AxelrodEventScheduler;
import modelarium.Config;
import modelarium.Model;
import modelarium.results.readonly.ReadOnlyResults;

import java.nio.file.Path;
import java.util.List;

/**
 * Modelarium implementation of Robert Axelrod's cultural dissemination model.
 *
 * <p>Each site has F cultural features, each initially assigned one of q traits uniformly at random. One randomly
 * selected site is activated per event, selects one of its cardinal neighbours, and interacts with probability
 * equal to their cultural similarity. On interaction, the active site copies one randomly selected differing trait
 * from that neighbour. Thus every successful interaction is locally convergent even though multiple globally
 * distinct cultural regions can survive.
 */
public final class AxelrodCulturalDisseminationMain {
    private static final String CONFIG_RESOURCE =
            "dev/modelarium/examples/axelrod_cultural_dissemination/axelrod-cultural-dissemination-config.json";

    private AxelrodCulturalDisseminationMain() {}

    public static void main(String[] args) {
        AxelrodCulturalDisseminationSettings settings =
                SettingsLoader.loadAxelrodCulturalDisseminationConfig(CONFIG_RESOURCE);
        validateSettings(settings);

        Config config = Config
                .builder()
                .populationSize(settings.populationSize())
                .tickCount(
                        settings.modelSettings().numOfEvents()
                                / settings.modelSettings().eventsPerModelariumTick()
                                + 1
                )
                // Axelrod's original process is asynchronous: exactly one randomly selected site is activated per
                // event. One synchronised worker preserves that semantics while still allowing the coordinator's
                // environment to measure the population between event batches.
                .threadCount(1)
                .areThreadsSynced(true)
                .agentGenerator(new AxelrodAgentGenerator(settings))
                .environmentGenerator(new AxelrodEnvironmentGenerator(settings))
                .scheduler(new AxelrodEventScheduler(
                        settings.grid().width(),
                        settings.grid().height(),
                        settings.modelSettings().numOfEvents(),
                        settings.modelSettings().eventsPerModelariumTick()
                ))
                .seed(settings.modelSettings().seed())
                .build();

        Model model = new Model(config);
        model.run();

        ReadOnlyResults results = model.getResults();
        printSummary(results, settings);

        Path outputPath = results.export("modelarium-examples/output/axelrod_cultural_dissemination");
        System.out.println("Results exported to: " + outputPath.toAbsolutePath());
    }

    private static void printSummary(
            ReadOnlyResults results,
            AxelrodCulturalDisseminationSettings settings
    ) {
        List<Integer> regionCounts = results.environment().attributeLogs(
                "cultural_metrics",
                "cultural_region_count",
                Integer.class
        );
        List<Integer> largestRegionSizes = results.environment().attributeLogs(
                "cultural_metrics",
                "largest_cultural_region_size",
                Integer.class
        );
        List<Integer> potentialPairs = results.environment().attributeLogs(
                "cultural_metrics",
                "potential_interaction_pair_count",
                Integer.class
        );
        List<Double> meanSimilarities = results.environment().attributeLogs(
                "cultural_metrics",
                "mean_neighbour_similarity",
                Double.class
        );

        if (regionCounts.isEmpty())
            return;

        int finalIndex = regionCounts.size() - 1;
        int firstMeasuredStableEvent = firstMeasuredStableEvent(
                potentialPairs,
                settings.modelSettings().eventsPerModelariumTick()
        );

        System.out.println("Axelrod cultural dissemination summary:");
        System.out.printf(
                "  Cultural regions: %d -> %d%n",
                regionCounts.getFirst(),
                regionCounts.get(finalIndex)
        );
        System.out.printf(
                "  Largest cultural region: %d -> %d sites (of %d)%n",
                largestRegionSizes.getFirst(),
                largestRegionSizes.get(finalIndex),
                settings.populationSize()
        );
        System.out.printf(
                "  Mean neighbour similarity: %.3f -> %.3f%n",
                meanSimilarities.getFirst(),
                meanSimilarities.get(finalIndex)
        );
        System.out.printf(
                "  Potentially interacting neighbour pairs: %d -> %d%n",
                potentialPairs.getFirst(),
                potentialPairs.get(finalIndex)
        );

        if (firstMeasuredStableEvent >= 0) {
            System.out.printf(
                    "  First measured stable state: by event %,d (metrics sampled every %,d events)%n",
                    firstMeasuredStableEvent,
                    settings.modelSettings().metricMeasurementIntervalEvents()
            );
        } else {
            System.out.printf(
                    "  Stable state not yet observed after %,d events%n",
                    settings.modelSettings().numOfEvents()
            );
        }
    }

    private static int firstMeasuredStableEvent(List<Integer> potentialPairs, int eventsPerModelariumTick) {
        for (int tick = 0; tick < potentialPairs.size(); tick++) {
            if (potentialPairs.get(tick) == 0)
                return tick * eventsPerModelariumTick;
        }
        return -1;
    }

    private static void validateSettings(AxelrodCulturalDisseminationSettings settings) {
        if (settings.grid().width() <= 0 || settings.grid().height() <= 0)
            throw new IllegalArgumentException("Grid width and height must be greater than 0");

        if (settings.culture().numOfFeatures() <= 0)
            throw new IllegalArgumentException("numOfFeatures must be greater than 0");

        if (settings.culture().traitsPerFeature() <= 1)
            throw new IllegalArgumentException("traitsPerFeature must be greater than 1");

        if (settings.modelSettings().numOfEvents() <= 0)
            throw new IllegalArgumentException("numOfEvents must be greater than 0");

        if (settings.modelSettings().eventsPerModelariumTick() <= 0)
            throw new IllegalArgumentException("eventsPerModelariumTick must be greater than 0");

        if (settings.modelSettings().metricMeasurementIntervalEvents() <= 0)
            throw new IllegalArgumentException("metricMeasurementIntervalEvents must be greater than 0");

        if (settings.modelSettings().numOfEvents() % settings.modelSettings().eventsPerModelariumTick() != 0)
            throw new IllegalArgumentException("numOfEvents must be divisible by eventsPerModelariumTick");

        if (settings.modelSettings().metricMeasurementIntervalEvents()
                % settings.modelSettings().eventsPerModelariumTick() != 0)
            throw new IllegalArgumentException(
                    "metricMeasurementIntervalEvents must be divisible by eventsPerModelariumTick"
            );

        if (settings.modelSettings().numOfEvents()
                % settings.modelSettings().metricMeasurementIntervalEvents() != 0)
            throw new IllegalArgumentException(
                    "numOfEvents must be divisible by metricMeasurementIntervalEvents so the final state is measured"
            );
    }
}
