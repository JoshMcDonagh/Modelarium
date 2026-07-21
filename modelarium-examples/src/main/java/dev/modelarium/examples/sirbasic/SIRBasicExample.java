package dev.modelarium.examples.sirbasic;

import dev.modelarium.examples.sirbasic.config.SettingsLoader;
import dev.modelarium.examples.sirbasic.config.SIRSettings;
import dev.modelarium.examples.sirbasic.entities.agents.SIRAgentGenerator;
import dev.modelarium.examples.sirbasic.entities.environment.SIREnvironmentGenerator;
import modelarium.Config;
import modelarium.Model;
import modelarium.results.Results;
import modelarium.scheduler.RandomOrderScheduler;

public class SIRBasicExample {
    public static void main(String[] args) {
        SIRSettings sirSettings = SettingsLoader.loadSIRConfig("sir-config.json");

        Config config = Config
                .builder()
                .populationSize(sirSettings.initialStates().S()
                        + sirSettings.initialStates().I()
                        + sirSettings.initialStates().R())
                .tickCount(sirSettings.modelSettings().numOfTicks())
                .threadCount(sirSettings.modelSettings().numOfCores())
                .areThreadsSynced(true)
                .agentGenerator(new SIRAgentGenerator())
                .environmentGenerator(new SIREnvironmentGenerator())
                .scheduler(new RandomOrderScheduler())
                .build();

        Model model = new Model(config);

        model.run();

        Results results = model.getResults();
    }
}
