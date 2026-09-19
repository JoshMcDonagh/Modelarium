package dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents;

import dev.modelarium.examples.axelrod_cultural_dissemination.config.AxelrodCulturalDisseminationSettings;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.attributes.culture.Culture;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.attributes.culture.CultureProperty;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.attributes.geography.GridPosition;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.attributes.geography.LocationProperty;
import modelarium.Config;
import modelarium.entities.Agent;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.sets.AgentAttributeSet;
import modelarium.entities.generators.DefaultAgentGenerator;

import java.util.List;
import java.util.random.RandomGenerator;

/** Generates one stationary agent per lattice site with an independently random initial culture. */
public final class AxelrodAgentGenerator extends DefaultAgentGenerator {
    private final AxelrodCulturalDisseminationSettings settings;
    private int generatedAgentCount = 0;

    public AxelrodAgentGenerator(AxelrodCulturalDisseminationSettings settings) {
        this.settings = settings;
    }

    @Override
    protected Agent generateAgent(Config config, RandomGenerator random) {
        int x = generatedAgentCount % settings.grid().width();
        int y = generatedAgentCount / settings.grid().width();

        LocationProperty locationProperty = new LocationProperty();
        locationProperty.set(new GridPosition(x, y));

        int[] traits = new int[settings.culture().numOfFeatures()];
        for (int feature = 0; feature < traits.length; feature++)
            traits[feature] = random.nextInt(settings.culture().traitsPerFeature());

        CultureProperty cultureProperty = new CultureProperty();
        cultureProperty.set(new Culture(traits));

        List<Attribute> geographyAttributes = List.of(locationProperty);
        List<Attribute> cultureAttributes = List.of(cultureProperty);

        Agent agent = new Agent(
                "site_" + x + "_" + y,
                List.of(
                        new AgentAttributeSet("geography", geographyAttributes),
                        new AgentAttributeSet("culture", cultureAttributes)
                )
        );

        generatedAgentCount++;
        return agent;
    }
}
