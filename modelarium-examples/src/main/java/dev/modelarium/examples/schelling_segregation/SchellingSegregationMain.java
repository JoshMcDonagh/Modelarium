package dev.modelarium.examples.schelling_segregation;

import dev.modelarium.examples.schelling_segregation.config.SchellingSegregationSettings;
import dev.modelarium.examples.schelling_segregation.config.SettingsLoader;
import dev.modelarium.examples.schelling_segregation.entities.agents.SchellingAgentGenerator;
import dev.modelarium.examples.schelling_segregation.entities.agents.attributes.demographics.Group;
import dev.modelarium.examples.schelling_segregation.entities.agents.attributes.location.Cell;
import dev.modelarium.examples.schelling_segregation.entities.environment.SchellingEnvironmentGenerator;
import modelarium.Config;
import modelarium.Model;
import modelarium.results.readonly.ReadOnlyResults;
import modelarium.scheduler.RandomOrderScheduler;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Modelarium implementation of the canonical two-group Schelling segregation model.
 *
 * <p>Agents begin randomly distributed across a grid with a configurable fraction of vacant cells. An agent is
 * satisfied when at least the configured fraction of its occupied Moore-neighbourhood belongs to the same group.
 * Dissatisfied agents relocate to vacant cells. Repeated local decisions generate substantially more segregated
 * neighbourhoods even though no agent has a global preference for segregation.
 */
public final class SchellingSegregationMain {
    private static final String CONFIG_RESOURCE =
            "dev/modelarium/examples/schelling_segregation/schelling-segregation-config.json";

    private SchellingSegregationMain() {}

    public static void main(String[] args) {
        SchellingSegregationSettings settings =
                SettingsLoader.loadSchellingSegregationConfig(CONFIG_RESOURCE);
        validateSettings(settings);

        Config config = Config
                .builder()
                .populationSize(settings.populationSize())
                .tickCount(settings.modelSettings().numOfTicks())
                .threadCount(settings.modelSettings().numOfCores())
                .areThreadsSynced(true)
                .agentGenerator(new SchellingAgentGenerator(settings))
                .environmentGenerator(new SchellingEnvironmentGenerator(settings))
                .scheduler(new RandomOrderScheduler())
                .seed(settings.modelSettings().seed())
                .build();

        Model model = new Model(config);
        model.run();

        ReadOnlyResults results = model.getResults();
        printSummary(results);

        Path outputPath = results.export("modelarium-examples/output/schelling_segregation");
        System.out.println("Results exported to: " + outputPath.toAbsolutePath());
    }

    private static void printSummary(ReadOnlyResults results) {
        Map<String, Map<String, Map<String, List<Object>>>> logs = results.agents().allLogs();
        if (logs.isEmpty())
            return;

        int ticks = logs.values().iterator().next().get("segregation").get("satisfied").size();
        if (ticks == 0)
            return;

        int firstTick = 0;
        int finalTick = ticks - 1;

        double initialSimilarity = meanSimilarity(logs, firstTick);
        double finalSimilarity = meanSimilarity(logs, finalTick);
        double initialSatisfied = satisfiedFraction(logs, firstTick);
        double finalSatisfied = satisfiedFraction(logs, finalTick);

        System.out.println("Schelling segregation summary:");
        System.out.printf("  Mean similar-neighbour fraction: %.3f -> %.3f%n", initialSimilarity, finalSimilarity);
        System.out.printf("  Satisfied agents: %.1f%% -> %.1f%%%n", initialSatisfied * 100.0, finalSatisfied * 100.0);
        System.out.printf("  Occupied cells in final logged state: %d%n", uniqueLocationCount(logs, finalTick));
    }

    private static double meanSimilarity(
            Map<String, Map<String, Map<String, List<Object>>>> logs,
            int tick
    ) {
        return logs.values().stream()
                .mapToDouble(agentLogs -> (Double) agentLogs
                        .get("segregation")
                        .get("similar_neighbour_fraction")
                        .get(tick))
                .average()
                .orElse(Double.NaN);
    }

    private static double satisfiedFraction(
            Map<String, Map<String, Map<String, List<Object>>>> logs,
            int tick
    ) {
        long satisfied = logs.values().stream()
                .filter(agentLogs -> Boolean.TRUE.equals(agentLogs
                        .get("segregation")
                        .get("satisfied")
                        .get(tick)))
                .count();

        return (double) satisfied / logs.size();
    }

    private static int uniqueLocationCount(
            Map<String, Map<String, Map<String, List<Object>>>> logs,
            int tick
    ) {
        Map<Cell, Group> occupancy = new HashMap<>();
        for (Map<String, Map<String, List<Object>>> agentLogs : logs.values()) {
            Cell cell = (Cell) agentLogs.get("location").get("location").get(tick);
            Group group = (Group) agentLogs.get("demographics").get("group").get(tick);
            occupancy.put(cell, group);
        }
        return occupancy.size();
    }

    private static void validateSettings(SchellingSegregationSettings settings) {
        if (settings.grid().width() <= 0 || settings.grid().height() <= 0)
            throw new IllegalArgumentException("Grid width and height must be greater than 0");

        if (settings.population().vacancyProportion() <= 0.0
                || settings.population().vacancyProportion() >= 1.0)
            throw new IllegalArgumentException("vacancyProportion must be greater than 0 and less than 1");

        if (settings.population().groupOneProportion() <= 0.0
                || settings.population().groupOneProportion() >= 1.0)
            throw new IllegalArgumentException("groupOneProportion must be greater than 0 and less than 1");

        if (settings.segregation().minimumSimilarNeighbourFraction() < 0.0
                || settings.segregation().minimumSimilarNeighbourFraction() > 1.0)
            throw new IllegalArgumentException("minimumSimilarNeighbourFraction must be between 0 and 1");

        if (settings.modelSettings().numOfCores() <= 0)
            throw new IllegalArgumentException("numOfCores must be greater than 0");

        if (settings.modelSettings().numOfTicks() <= 0)
            throw new IllegalArgumentException("numOfTicks must be greater than 0");

        if (settings.populationSize() <= 0 || settings.populationSize() >= settings.cellCount())
            throw new IllegalArgumentException("Configuration must produce at least one agent and one vacant cell");
    }
}
