package dev.modelarium.examples.smoke;

import dev.modelarium.examples.axelrod_cultural_dissemination.config.AxelrodCulturalDisseminationSettings;
import dev.modelarium.examples.axelrod_cultural_dissemination.config.SettingsLoader;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.AxelrodAgentGenerator;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.environment.AxelrodEnvironmentGenerator;
import dev.modelarium.examples.axelrod_cultural_dissemination.scheduler.AxelrodEventScheduler;
import modelarium.Config;
import modelarium.Model;
import modelarium.results.readonly.ReadOnlyResults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AxelrodCulturalDisseminationSmokeTest {
    private static final String CONFIG_RESOURCE =
            "dev/modelarium/examples/axelrod_cultural_dissemination/axelrod-cultural-dissemination-config.json";

    @TempDir
    Path temporaryDirectory;

    @Test
    @Timeout(20)
    void reducedModelLoadsRunsHonoursEventLimitAndExports() {
        assertNotNull(SettingsLoader.loadAxelrodCulturalDisseminationConfig(CONFIG_RESOURCE));

        int width = 3;
        int height = 3;
        int maximumEvents = 50;
        int checkInterval = 10;
        AxelrodCulturalDisseminationSettings settings = new AxelrodCulturalDisseminationSettings(
                new AxelrodCulturalDisseminationSettings.ModelSettings(1, maximumEvents, checkInterval, 1997L),
                new AxelrodCulturalDisseminationSettings.Grid(width, height),
                new AxelrodCulturalDisseminationSettings.Culture(3, 4)
        );
        AxelrodEventScheduler scheduler = new AxelrodEventScheduler(
                width,
                height,
                maximumEvents,
                checkInterval
        );

        Config config = Config.builder()
                .populationSize(settings.populationSize())
                .tickCount(2)
                .threadCount(1)
                .areThreadsSynced(true)
                .agentGenerator(new AxelrodAgentGenerator(settings))
                .environmentGenerator(new AxelrodEnvironmentGenerator(settings))
                .scheduler(scheduler)
                .seed(settings.modelSettings().baseSeed())
                .build();

        Model model = new Model(config);
        assertDoesNotThrow(model::run);

        assertTrue(scheduler.eventsProcessed() >= 0);
        assertTrue(scheduler.eventsProcessed() <= maximumEvents);

        ReadOnlyResults results = model.getResults();
        assertEquals(settings.populationSize(), results.agents().agentLogCount());
        assertEquals(0, results.agents().attributeLogCount("site_0_0", "geography"));
        assertEquals(0, results.agents().attributeLogCount("site_0_0", "culture"));

        List<Integer> regionCounts = results.environment().attributeLogs(
                "cultural_metrics",
                "cultural_region_count",
                Integer.class
        );
        assertEquals(2, regionCounts.size());
        assertTrue(regionCounts.stream().allMatch(count -> count >= 1 && count <= settings.populationSize()));

        Path exportPath = results.export(temporaryDirectory);
        assertTrue(Files.isDirectory(exportPath));
        assertTrue(Files.isRegularFile(exportPath.resolve("config.json")));
    }
}
