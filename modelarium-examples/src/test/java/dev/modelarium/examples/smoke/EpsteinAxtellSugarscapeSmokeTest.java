package dev.modelarium.examples.smoke;

import dev.modelarium.examples.epstein_axtell_sugarscape.config.SettingsLoader;
import dev.modelarium.examples.epstein_axtell_sugarscape.entities.agents.SugarscapeAgentGenerator;
import dev.modelarium.examples.epstein_axtell_sugarscape.entities.environment.SugarscapeEnvironmentGenerator;
import dev.modelarium.examples.epstein_axtell_sugarscape.scheduler.SugarscapeRunSpec;
import dev.modelarium.examples.epstein_axtell_sugarscape.scheduler.SugarscapeScheduler;
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

class EpsteinAxtellSugarscapeSmokeTest {
    private static final String CONFIG_RESOURCE =
            "dev/modelarium/examples/epstein_axtell_sugarscape/sugarscape-config.json";

    @TempDir
    Path temporaryDirectory;

    @Test
    @Timeout(20)
    void reducedModelLoadsRunsRecordsMetricsAndExports() {
        assertNotNull(SettingsLoader.load(CONFIG_RESOURCE));

        int populationSize = 12;
        int ticks = 3;
        SugarscapeRunSpec specification = SugarscapeRunSpec.standard(
                "smoke_test",
                populationSize,
                ticks,
                SugarscapeRunSpec.GrowthMode.CONSTANT
        );
        SugarscapeScheduler scheduler = new SugarscapeScheduler(specification);

        Config config = Config.builder()
                .populationSize(populationSize)
                .tickCount(ticks)
                .threadCount(1)
                .areThreadsSynced(true)
                .agentGenerator(new SugarscapeAgentGenerator(specification))
                .environmentGenerator(new SugarscapeEnvironmentGenerator())
                .scheduler(scheduler)
                .seed(1996L)
                .build();

        Model model = new Model(config);
        assertDoesNotThrow(model::run);

        List<SugarscapeScheduler.Metrics> metrics = scheduler.metrics();
        assertEquals(ticks + 1, metrics.size());
        assertEquals(0, metrics.getFirst().step());
        assertEquals(ticks, metrics.getLast().step());
        assertTrue(metrics.stream().allMatch(metric -> metric.population() >= 0));
        assertTrue(metrics.stream().allMatch(metric -> metric.population() <= populationSize));

        ReadOnlyResults results = model.getResults();
        assertEquals(populationSize, results.agents().agentLogCount());
        assertEquals(ticks, results.agents().attributeLogs("agent_0", "state", "wealth").size());
        assertEquals(2L * SugarscapeScheduler.WIDTH * SugarscapeScheduler.HEIGHT, scheduler.snapshots().size());

        Path exportPath = results.export(temporaryDirectory);
        assertTrue(Files.isDirectory(exportPath));
        assertTrue(Files.isRegularFile(exportPath.resolve("config.json")));
    }
}
