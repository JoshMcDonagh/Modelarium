package dev.modelarium.examples.sir.entities.agents;

import dev.modelarium.examples.sir.config.SIRSettings;
import dev.modelarium.examples.sir.entities.agents.attributes.location.Coordinates;
import dev.modelarium.examples.sir.entities.agents.attributes.location.LocationProperty;
import dev.modelarium.examples.sir.entities.agents.attributes.sir.InfectedEvent;
import dev.modelarium.examples.sir.entities.agents.attributes.sir.RecoveredEvent;
import dev.modelarium.examples.sir.entities.agents.attributes.sir.SIRState;
import dev.modelarium.examples.sir.entities.agents.attributes.sir.SIRStateProperty;
import modelarium.Config;
import modelarium.entities.Agent;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.sets.AgentAttributeSet;
import modelarium.entities.generators.DefaultAgentGenerator;

import java.util.ArrayList;
import java.util.Objects;
import java.util.random.RandomGenerator;

public class SIRAgentGenerator extends DefaultAgentGenerator {
    private int agentCount = 0;

    private final SIRSettings sirSettings;

    public SIRAgentGenerator(SIRSettings sirSettings) {
        this.sirSettings = Objects.requireNonNull(sirSettings, "sirSettings must be set");
    }

    @Override
    protected Agent generateAgent(Config config, RandomGenerator random) {
        ArrayList<AgentAttributeSet> agentAttributeSets = new ArrayList<>();

        LocationProperty locationProperty = new LocationProperty(sirSettings);
        ArrayList<Attribute> agentLocationAttributes = new ArrayList<>();
        agentLocationAttributes.add(locationProperty);
        int x = random.nextInt(0, sirSettings.environment().area().width());
        int y = random.nextInt(0, sirSettings.environment().area().height());
        locationProperty.set(new Coordinates(x, y));
        agentAttributeSets.add(new AgentAttributeSet("location", agentLocationAttributes));

        SIRStateProperty sirStateProperty = new SIRStateProperty();
        ArrayList<Attribute> agentSIRAttributes = new ArrayList<>();
        agentSIRAttributes.add(sirStateProperty);
        agentSIRAttributes.add(new RecoveredEvent(sirSettings));
        agentSIRAttributes.add(new InfectedEvent(sirSettings));
        if (agentCount < sirSettings.initialStates().S())
            sirStateProperty.set(SIRState.SUSCEPTIBLE);
        else if (agentCount < sirSettings.initialStates().I() + sirSettings.initialStates().S())
            sirStateProperty.set(SIRState.INFECTIOUS);
        else if (agentCount < sirSettings.initialStates().I() + sirSettings.initialStates().S()
                + sirSettings.initialStates().R())
            sirStateProperty.set(SIRState.RECOVERED);
        else
            throw new IllegalStateException("Agent cannot be generated - all initial SIR states have already been " +
                    "assigned.");

        agentAttributeSets.add(new AgentAttributeSet("sir", agentSIRAttributes));

        Agent newAgent = new Agent("agent_" + agentCount, agentAttributeSets);
        agentCount++;
        return newAgent;
    }

    @Override
    protected void reset() {
        agentCount = 0;
    }
}
