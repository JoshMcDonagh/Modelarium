package dev.modelarium.examples.smoke;

import dev.modelarium.examples.sir.config.SIRSettings;
import dev.modelarium.examples.sir.config.SettingsLoader;
import dev.modelarium.examples.sir.entities.agents.SIRAgentGenerator;
import dev.modelarium.examples.sir.entities.environment.SIREnvironmentGenerator;
import modelarium.Config;
import modelarium.Model;
import modelarium.results.readonly.ReadOnlyResults;
import modelarium.scheduler.RandomOrderScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SIRSmokeTest {
    private static final String CONFIG_RESOURCE = "dev/modelarium/examples/sir/sir-config.json";

    @TempDir
    Path temporaryDirectory;

    @Test
    @Timeout(20)
    void reducedModelLoadsRunsAndExports() {
        SIRSettings settings = SettingsLoader.loadSIRConfig(CONFIG_RESOURCE);
        assertNotNull(settings);
        assertTrue(settings.initialStates().S() + settings.initialStates().I() + settings.initialStates().R() > 0);

        int populationSize = 12;
        int ticks = 3;
        Config config = Config.builder()
                .populationSize(populationSize)
                .tickCount(ticks)
                .threadCount(1)
                .areThreadsSynced(true)
                .agentGenerator(new SIRAgentGenerator())
                .environmentGenerator(new SIREnvironmentGenerator())
                .scheduler(new RandomOrderScheduler())
                .seed(1976L)
                .build();

        Model model = new Model(config);
        assertDoesNotThrow(model::run);

        ReadOnlyResults results = model.getResults();
        assertEquals(populationSize, results.agents().agentLogCount());
        assertEquals(
                ticks,
                results.agents().attributeLogs("agent_0", "sir", "sir_state").size()
        );
        assertEquals(
                ticks,
                results.environment().attributeLogs("prevalence", "number_of_infected").size()
        );

        Path exportPath = results.export(temporaryDirectory);
        assertTrue(Files.isDirectory(exportPath));
        assertTrue(Files.isRegularFile(exportPath.resolve("config.json")));
    }

    @Test
    @Timeout(20)
    void sameGeneratorCanBeReusedForConsecutiveRuns() {
        int populationSize = 12;
        SIRAgentGenerator generator = new SIRAgentGenerator();
        Config config = Config.builder()
                .populationSize(populationSize)
                .tickCount(1)
                .threadCount(1)
                .areThreadsSynced(true)
                .agentGenerator(generator)
                .environmentGenerator(new SIREnvironmentGenerator())
                .scheduler(new RandomOrderScheduler())
                .seed(1976L)
                .build();
        Model model = new Model(config);

        model.run();
        model.run();

        ReadOnlyResults secondRunResults = model.getResults();
        assertEquals(populationSize, secondRunResults.agents().agentLogCount());
        assertEquals(1, secondRunResults.agents().attributeLogs("agent_0", "sir", "sir_state").size());
    }
}
