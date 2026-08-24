package dev.modelarium.examples.sir.entities.environment.attributes.prevalence;

import dev.modelarium.examples.sir.entities.agents.attributes.sir.SIRState;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;

public class Prevalence {
    private int numberOfInfected;
    private double infectedPercentage;

    void update(EnvironmentContext context) {
        int currentPopulationSize = context.getCurrentPopulationSize();

        numberOfInfected = context.getFilteredAgents(
                agent -> agent.getProperty("sir", "sir_state").get() == SIRState.INFECTIOUS
        ).size();

        infectedPercentage = numberOfInfected / (double) currentPopulationSize;
    }

    int numberOfInfected() {
        return numberOfInfected;
    }

    double infectedPercentage() {
        return infectedPercentage;
    }
}
