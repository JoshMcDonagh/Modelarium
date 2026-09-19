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
import java.util.List;

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

        SIRSettings testSettings = settingsWith(10, 1, 1, 3, settings.modelSettings().seed());
        int populationSize = testSettings.populationSize();
        int ticks = testSettings.modelSettings().numOfTicks();
        Config config = configFor(testSettings);

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
        SIRSettings settings = settingsWith(10, 1, 1, 1, 1976L);
        int populationSize = settings.populationSize();
        SIRAgentGenerator generator = new SIRAgentGenerator(settings);
        Config config = Config.builder()
                .populationSize(populationSize)
                .tickCount(settings.modelSettings().numOfTicks())
                .threadCount(settings.modelSettings().numOfCores())
                .areThreadsSynced(true)
                .agentGenerator(generator)
                .environmentGenerator(new SIREnvironmentGenerator(settings))
                .scheduler(new RandomOrderScheduler())
                .seed(settings.modelSettings().seed())
                .build();
        Model model = new Model(config);

        model.run();
        model.run();

        ReadOnlyResults secondRunResults = model.getResults();
        assertEquals(populationSize, secondRunResults.agents().agentLogCount());
        assertEquals(1, secondRunResults.agents().attributeLogs("agent_0", "sir", "sir_state").size());
    }

    @Test
    @Timeout(20)
    void sameSeedProducesSameResults() {
        SIRSettings settings = settingsWith(10, 1, 1, 4, 1976L);
        Model firstModel = new Model(configFor(settings));
        Model secondModel = new Model(configFor(settings));

        firstModel.run();
        secondModel.run();

        ReadOnlyResults first = firstModel.getResults();
        ReadOnlyResults second = secondModel.getResults();
        for (int i = 0; i < settings.populationSize(); i++) {
            String agentName = "agent_" + i;
            List<String> firstLocations = first.agents()
                    .attributeLogs(agentName, "location", "location")
                    .stream()
                    .map(Object::toString)
                    .toList();
            List<String> secondLocations = second.agents()
                    .attributeLogs(agentName, "location", "location")
                    .stream()
                    .map(Object::toString)
                    .toList();

            assertEquals(firstLocations, secondLocations);
            assertEquals(
                    first.agents().attributeLogs(agentName, "sir", "sir_state"),
                    second.agents().attributeLogs(agentName, "sir", "sir_state")
            );
        }
        assertEquals(first.environment().environmentLogs(), second.environment().environmentLogs());
    }

    private static Config configFor(SIRSettings settings) {
        return Config.builder()
                .populationSize(settings.populationSize())
                .tickCount(settings.modelSettings().numOfTicks())
                .threadCount(settings.modelSettings().numOfCores())
                .areThreadsSynced(true)
                .agentGenerator(new SIRAgentGenerator(settings))
                .environmentGenerator(new SIREnvironmentGenerator(settings))
                .scheduler(new RandomOrderScheduler())
                .seed(settings.modelSettings().seed())
                .build();
    }

    private static SIRSettings settingsWith(int susceptible, int infectious, int recovered, int ticks, long seed) {
        return new SIRSettings(
                new SIRSettings.SIRModelSettings(1, ticks, seed),
                new SIRSettings.InitialStates(susceptible, infectious, recovered),
                new SIRSettings.Environment(new SIRSettings.Environment.Area(20, 20)),
                new SIRSettings.Movement(0.5, 2.0),
                new SIRSettings.Disease(0.5, 0.02)
        );
    }
}
