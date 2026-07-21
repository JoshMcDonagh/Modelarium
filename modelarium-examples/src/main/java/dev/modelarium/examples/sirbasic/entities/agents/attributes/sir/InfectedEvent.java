package dev.modelarium.examples.sirbasic.entities.agents.attributes.sir;

import dev.modelarium.examples.sirbasic.config.SettingsLoader;
import dev.modelarium.examples.sirbasic.config.SIRSettings;
import dev.modelarium.examples.sirbasic.entities.agents.attributes.location.Coordinates;
import modelarium.entities.agents.Agent;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.immutable.ImmutableAgentSet;

import java.util.function.Predicate;

public class InfectedEvent extends AgentEvent {
    private static double euclideanDistance(Coordinates coordinates1, Coordinates coordinates2) {
        double dx = coordinates2.getX() - coordinates1.getX();
        double dy = coordinates2.getY() - coordinates1.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private final double infectionProbabilityPerContact;
    private final double contactDistance;

    public InfectedEvent() {
        super("infected", false, AttributeAccessLevel.PRIVATE);
        SIRSettings sirSettings = SettingsLoader.loadSIRConfig("sir-config.json");
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

        Predicate<Agent> nearbyAndInfectiousOnly = agent -> {
            Coordinates otherCoordinates = (Coordinates) agent
                    .getProperty("location", "location")
                    .get();

            if (euclideanDistance(coordinates, otherCoordinates) > contactDistance)
                return false;

            SIRState otherSirState = (SIRState) context
                    .getThisEntity()
                    .getProperty("sir", "location")
                    .get();

            return otherSirState == SIRState.INFECTIOUS;
        };

        ImmutableAgentSet otherAgentsNearby = context.getFilteredAgents(nearbyAndInfectiousOnly);

        if (otherAgentsNearby.isEmpty())
            return false;

        return context.getRandom().nextDouble(0.0, 1.0) < infectionProbabilityPerContact;
    }

    @Override
    protected void run(AgentContext context) {
        ((SIRStateProperty) context
                .getThisEntity()
                .getProperty("sir", "sir_state"))
                .set(SIRState.INFECTIOUS);
    }
}
