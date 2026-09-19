package dev.modelarium.examples.smoke;

import dev.modelarium.examples.schelling_segregation.config.SchellingSegregationSettings;
import dev.modelarium.examples.schelling_segregation.config.SettingsLoader;
import dev.modelarium.examples.schelling_segregation.entities.agents.SchellingAgentGenerator;
import dev.modelarium.examples.schelling_segregation.entities.agents.attributes.location.Cell;
import dev.modelarium.examples.schelling_segregation.entities.environment.SchellingEnvironmentGenerator;
import modelarium.Config;
import modelarium.Model;
import modelarium.results.readonly.ReadOnlyResults;
import modelarium.scheduler.RandomOrderScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SchellingSegregationSmokeTest {
    private static final String CONFIG_RESOURCE =
            "dev/modelarium/examples/schelling_segregation/schelling-segregation-config.json";

    @TempDir
    Path temporaryDirectory;

    @Test
    @Timeout(20)
    void reducedModelLoadsRunsPreservesUniqueLocationsAndExports() {
        assertNotNull(SettingsLoader.loadSchellingSegregationConfig(CONFIG_RESOURCE));

        int ticks = 4;
        SchellingSegregationSettings settings = new SchellingSegregationSettings(
                new SchellingSegregationSettings.ModelSettings(2, ticks, 1971L),
                new SchellingSegregationSettings.Grid(4, 4),
                new SchellingSegregationSettings.Population(0.25, 0.5),
                new SchellingSegregationSettings.Segregation(0.30)
        );

        Config config = Config.builder()
                .populationSize(settings.populationSize())
                .tickCount(ticks)
                .threadCount(settings.modelSettings().numOfCores())
                .areThreadsSynced(true)
                .agentGenerator(new SchellingAgentGenerator(settings))
                .environmentGenerator(new SchellingEnvironmentGenerator(settings))
                .scheduler(new RandomOrderScheduler())
                .seed(settings.modelSettings().seed())
                .build();

        Model model = new Model(config);
        assertDoesNotThrow(model::run);

        ReadOnlyResults results = model.getResults();
        assertEquals(settings.populationSize(), results.agents().agentLogCount());

        Set<Cell> finalLocations = new HashSet<>();
        for (Map<String, Map<String, java.util.List<Object>>> agentLogs : results.agents().allLogs().values()) {
            java.util.List<Object> locations = agentLogs.get("location").get("location");
            assertEquals(ticks, locations.size());
            finalLocations.add((Cell) locations.getLast());
        }
        assertEquals(settings.populationSize(), finalLocations.size());

        Path exportPath = results.export(temporaryDirectory);
        assertTrue(Files.isDirectory(exportPath));
        assertTrue(Files.isRegularFile(exportPath.resolve("config.json")));
    }
}
