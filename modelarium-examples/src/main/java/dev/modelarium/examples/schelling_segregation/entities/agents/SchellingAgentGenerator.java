package dev.modelarium.examples.schelling_segregation.entities.agents;

import dev.modelarium.examples.schelling_segregation.config.SchellingSegregationSettings;
import dev.modelarium.examples.schelling_segregation.entities.agents.attributes.demographics.Group;
import dev.modelarium.examples.schelling_segregation.entities.agents.attributes.demographics.GroupProperty;
import dev.modelarium.examples.schelling_segregation.entities.agents.attributes.location.Cell;
import dev.modelarium.examples.schelling_segregation.entities.agents.attributes.location.LocationProperty;
import dev.modelarium.examples.schelling_segregation.entities.agents.attributes.location.MoveProposalProperty;
import dev.modelarium.examples.schelling_segregation.entities.agents.attributes.location.RelocationRoutine;
import dev.modelarium.examples.schelling_segregation.entities.agents.attributes.segregation.SatisfiedProperty;
import dev.modelarium.examples.schelling_segregation.entities.agents.attributes.segregation.SimilarNeighbourFractionProperty;
import modelarium.Config;
import modelarium.entities.Agent;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.sets.AgentAttributeSet;
import modelarium.entities.generators.DefaultAgentGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/** Generates Schelling agents with unique random initial locations and balanced random group membership. */
public final class SchellingAgentGenerator extends DefaultAgentGenerator {
    private final SchellingSegregationSettings settings;
    private final List<Cell> unassignedCells = new ArrayList<>();
    private final List<Group> unassignedGroups = new ArrayList<>();
    private int generatedAgentCount = 0;

    public SchellingAgentGenerator(SchellingSegregationSettings settings) {
        this.settings = settings;

        for (int x = 0; x < settings.grid().width(); x++) {
            for (int y = 0; y < settings.grid().height(); y++)
                unassignedCells.add(new Cell(x, y));
        }

        int populationSize = settings.populationSize();
        int groupOneCount = (int) Math.round(populationSize * settings.population().groupOneProportion());

        for (int i = 0; i < groupOneCount; i++)
            unassignedGroups.add(Group.GROUP_ONE);
        for (int i = groupOneCount; i < populationSize; i++)
            unassignedGroups.add(Group.GROUP_TWO);
    }

    @Override
    protected Agent generateAgent(Config config, RandomGenerator random) {
        Cell initialCell = removeRandom(unassignedCells, random);
        Group group = removeRandom(unassignedGroups, random);

        GroupProperty groupProperty = new GroupProperty();
        groupProperty.set(group);

        LocationProperty locationProperty = new LocationProperty();
        locationProperty.set(initialCell);

        List<Attribute> demographicAttributes = List.of(groupProperty);

        List<Attribute> locationAttributes = new ArrayList<>();
        locationAttributes.add(locationProperty);
        locationAttributes.add(new MoveProposalProperty());

        List<Attribute> segregationAttributes = new ArrayList<>();
        segregationAttributes.add(new SimilarNeighbourFractionProperty(
                settings.grid().width(),
                settings.grid().height()
        ));
        segregationAttributes.add(new SatisfiedProperty(
                settings.segregation().minimumSimilarNeighbourFraction()
        ));
        segregationAttributes.add(new RelocationRoutine(
                settings.grid().width(),
                settings.grid().height()
        ));

        Agent agent = new Agent(
                "agent_" + generatedAgentCount,
                List.of(
                        new AgentAttributeSet("demographics", demographicAttributes),
                        new AgentAttributeSet("location", locationAttributes),
                        new AgentAttributeSet("segregation", segregationAttributes)
                )
        );
        generatedAgentCount++;

        return agent;
    }

    private static <T> T removeRandom(List<T> values, RandomGenerator random) {
        if (values.isEmpty())
            throw new IllegalStateException("No values remain to assign");

        return values.remove(random.nextInt(values.size()));
    }
}
