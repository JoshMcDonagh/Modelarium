package dev.modelarium.examples.sir;

import dev.modelarium.examples.sir.config.SIRSettings;
import dev.modelarium.examples.sir.config.SettingsLoader;
import dev.modelarium.examples.sir.entities.agents.SIRAgentGenerator;
import dev.modelarium.examples.sir.entities.environment.SIREnvironmentGenerator;
import modelarium.Config;
import modelarium.Model;
import modelarium.scheduler.RandomOrderScheduler;

public class SIRMain {
    public static void main(String[] args) {
        SIRSettings sirSettings = SettingsLoader.loadSIRConfig("dev/modelarium/examples/sir/sir-config.json");

        Config config = Config
                .builder()
                .populationSize(sirSettings.populationSize())
                .tickCount(sirSettings.modelSettings().numOfTicks())
                .threadCount(sirSettings.modelSettings().numOfCores())
                .areThreadsSynced(true)
                .agentGenerator(new SIRAgentGenerator(sirSettings))
                .environmentGenerator(new SIREnvironmentGenerator(sirSettings))
                .scheduler(new RandomOrderScheduler())
                .seed(sirSettings.modelSettings().seed())
                .build();

        Model model = new Model(config);

        model.run();

        model.getResults().export("modelarium-examples/output/sir");
    }
}
