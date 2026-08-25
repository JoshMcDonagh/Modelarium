package dev.modelarium.examples.sir.entities.agents.attributes.sir;

import dev.modelarium.examples.sir.config.SIRSettings;
import dev.modelarium.examples.sir.config.SettingsLoader;
import dev.modelarium.examples.sir.entities.agents.attributes.location.Coordinates;
import modelarium.entities.readonly.ReadOnlyAgent;
import modelarium.entities.agentsets.ReadOnlyAgentSet;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.contexts.AgentContext;

import java.util.function.Predicate;

public class InfectedEvent extends AgentEvent {
    private static final Predicate<ReadOnlyAgent> INFECTIOUS_ONLY = agent -> {
        SIRState sirState = (SIRState) agent
                .getProperty("sir", "sir_state")
                .get();

        return sirState == SIRState.INFECTIOUS;
    };

    private static double euclideanDistance(Coordinates coordinates1, Coordinates coordinates2) {
        double dx = coordinates2.getX() - coordinates1.getX();
        double dy = coordinates2.getY() - coordinates1.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private final double infectionProbabilityPerContact;
    private final double contactDistance;

    public InfectedEvent() {
        super("infected", false, AttributeAccessLevel.PRIVATE);
        SIRSettings sirSettings = SettingsLoader.loadSIRConfig("dev/modelarium/examples/sir/sir-config.json");
        infectionProbabilityPerContact = sirSettings.disease().infectionProbabilityPerContact();
        contactDistance = sirSettings.movement().contactDistance();
    }

    @Override
    protected boolean isTriggered(AgentContext context) {
        SIRState sirState = ((SIRStateProperty) context
                .getThisEntity()
                .getProperty("sir", "sir_state"))
                .get();

        if (sirState != SIRState.SUSCEPTIBLE)
            return false;

        Coordinates coordinates = (Coordinates) context
                .getThisEntity()
                .getProperty("location", "location")
                .get();

        ReadOnlyAgentSet infectiousAgents =
                context.getFilteredAgents(INFECTIOUS_ONLY);

        for (ReadOnlyAgent infectiousAgent : infectiousAgents) {
            Coordinates infectiousAgentCoordinates =
                    (Coordinates) infectiousAgent
                            .getProperty("location", "location")
                            .get();

            if (euclideanDistance(
                    coordinates,
                    infectiousAgentCoordinates
            ) <= contactDistance) {
                return context
                        .getRandom()
                        .nextDouble(0.0, 1.0)
                        < infectionProbabilityPerContact;
            }
        }

        return false;
    }

    @Override
    protected void run(AgentContext context) {
        ((SIRStateProperty) context
                .getThisEntity()
                .getProperty("sir", "sir_state"))
                .set(SIRState.INFECTIOUS);
    }
}
