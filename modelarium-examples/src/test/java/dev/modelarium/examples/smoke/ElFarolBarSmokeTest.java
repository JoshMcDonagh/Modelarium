package dev.modelarium.examples.smoke;

import dev.modelarium.examples.el_farol_bar.config.ElFarolBarSettings;
import dev.modelarium.examples.el_farol_bar.config.SettingsLoader;
import dev.modelarium.examples.el_farol_bar.entities.agents.ElFarolBarAgentGenerator;
import dev.modelarium.examples.el_farol_bar.entities.environment.ElFarolBarEnvironmentGenerator;
import modelarium.Config;
import modelarium.Model;
import modelarium.results.readonly.ReadOnlyResults;
import modelarium.scheduler.RandomOrderScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ElFarolBarSmokeTest {
    private static final String CONFIG_RESOURCE =
            "dev/modelarium/examples/el_farol_bar/el-farol-bar-config.json";

    @TempDir
    Path temporaryDirectory;

    @Test
    @Timeout(20)
    void reducedModelLoadsRunsProducesBoundedAttendanceAndExports() {
        assertNotNull(SettingsLoader.loadElFarolBarConfig(CONFIG_RESOURCE));

        int populationSize = 12;
        int weeks = 5;
        ElFarolBarSettings settings = new ElFarolBarSettings(
                new ElFarolBarSettings.ModelSettings(2, weeks, 1, 1994L),
                new ElFarolBarSettings.Bar(7),
                new ElFarolBarSettings.Agents(populationSize, 4),
                new ElFarolBarSettings.Prediction(List.of(4, 8, 6, 3, 7))
        );

        Config config = Config.builder()
                .populationSize(populationSize)
                .tickCount(weeks)
                .threadCount(settings.modelSettings().numOfCores())
                .areThreadsSynced(true)
                .agentGenerator(new ElFarolBarAgentGenerator(settings))
                .environmentGenerator(new ElFarolBarEnvironmentGenerator(settings))
                .scheduler(new RandomOrderScheduler())
                .seed(settings.modelSettings().seed())
                .build();

        Model model = new Model(config);
        assertDoesNotThrow(model::run);

        ReadOnlyResults results = model.getResults();
        assertEquals(populationSize, results.agents().agentLogCount());
        assertEquals(weeks, results.agents().attributeLogs("agent_0", "decision", "attending").size());

        List<Integer> attendance = results.environment().attributeLogs("bar", "attendance", Integer.class);
        assertEquals(weeks, attendance.size());
        assertTrue(attendance.stream().allMatch(value -> value >= 0 && value <= populationSize));

        Path exportPath = results.export(temporaryDirectory);
        assertTrue(Files.isDirectory(exportPath));
        assertTrue(Files.isRegularFile(exportPath.resolve("config.json")));
    }
}
