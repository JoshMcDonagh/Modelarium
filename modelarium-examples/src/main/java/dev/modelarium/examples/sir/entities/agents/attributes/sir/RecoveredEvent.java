package dev.modelarium.examples.sir.entities.agents.attributes.sir;

import dev.modelarium.examples.sir.config.SettingsLoader;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.contexts.AgentContext;

import java.util.random.RandomGenerator;

public class RecoveredEvent extends AgentEvent {
    private final double recoveryProbabilityPerTick;

    public RecoveredEvent() {
        super("recovered", false, AttributeAccessLevel.PRIVATE);
        recoveryProbabilityPerTick = SettingsLoader
                .loadSIRConfig("dev/modelarium/examples/sirbasic/sir-config.json")
                .disease()
                .recoveryProbabilityPerTick();
    }

    @Override
    protected boolean isTriggered(AgentContext context) {
        SIRState state = (SIRState) context
                .getThisEntity()
                .getProperty("sir", "sir_state")
                .get();

        RandomGenerator random = context.getRandom();

        return state == SIRState.INFECTIOUS && random.nextDouble(0.0, 1.0) < recoveryProbabilityPerTick;
    }

    @Override
    protected void run(AgentContext context) {
        ((SIRStateProperty) context
                .getThisEntity()
                .getProperty("sir", "sir_state"))
                .set(SIRState.RECOVERED);
    }
}
