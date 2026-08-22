package dev.modelarium.examples.sirbasic.entities.agents;

import dev.modelarium.examples.sirbasic.config.SIRSettings;
import dev.modelarium.examples.sirbasic.config.SettingsLoader;
import dev.modelarium.examples.sirbasic.entities.agents.attributes.location.Coordinates;
import dev.modelarium.examples.sirbasic.entities.agents.attributes.location.LocationProperty;
import dev.modelarium.examples.sirbasic.entities.agents.attributes.sir.InfectedEvent;
import dev.modelarium.examples.sirbasic.entities.agents.attributes.sir.RecoveredEvent;
import dev.modelarium.examples.sirbasic.entities.agents.attributes.sir.SIRState;
import dev.modelarium.examples.sirbasic.entities.agents.attributes.sir.SIRStateProperty;
import modelarium.Config;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;

import java.util.ArrayList;
import java.util.random.RandomGenerator;

public class SIRAgentGenerator extends DefaultAgentGenerator {
    private static int agentCount = 0;
    private static int susceptibleAgentCount = 0;
    private static int infectiousAgentCount = 0;
    private static int recoveredAgentCount = 0;

    private final SIRSettings sirSettings;

    public SIRAgentGenerator() {
        sirSettings = SettingsLoader.loadSIRConfig("dev/modelarium/examples/sirbasic/sir-config.json");
    }

    @Override
    protected Agent generateAgent(Config config, RandomGenerator random) {
        ArrayList<MutableAgentAttributeSet> agentAttributeSets = new ArrayList<>();

        LocationProperty locationProperty = new LocationProperty();
        ArrayList<Attribute> agentLocationAttributes = new ArrayList<>();
        agentLocationAttributes.add(locationProperty);
        int x = random.nextInt(0, sirSettings.environment().area().width());
        int y = random.nextInt(0, sirSettings.environment().area().height());
        locationProperty.set(new Coordinates(x, y));
        agentAttributeSets.add(new MutableAgentAttributeSet("location", agentLocationAttributes));

        SIRStateProperty sirStateProperty = new SIRStateProperty();
        ArrayList<Attribute> agentSIRAttributes = new ArrayList<>();
        agentSIRAttributes.add(sirStateProperty);
        agentSIRAttributes.add(new RecoveredEvent());
        agentSIRAttributes.add(new InfectedEvent());
        if (susceptibleAgentCount < sirSettings.initialStates().S()) {
            sirStateProperty.set(SIRState.SUSCEPTIBLE);
            susceptibleAgentCount++;
        } else if (infectiousAgentCount < sirSettings.initialStates().I()) {
            sirStateProperty.set(SIRState.INFECTIOUS);
            infectiousAgentCount++;
        } else if (recoveredAgentCount < sirSettings.initialStates().R()) {
            sirStateProperty.set(SIRState.RECOVERED);
            recoveredAgentCount++;
        } else {
            throw new IllegalStateException("Agent cannot be generated - all initial SIR states have already been assigned.");
        }
        agentAttributeSets.add(new MutableAgentAttributeSet("sir", agentSIRAttributes));

        Agent newAgent = new Agent("agent_" + agentCount, agentAttributeSets);
        agentCount++;
        return newAgent;
    }
}
