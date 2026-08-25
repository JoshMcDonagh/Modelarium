package dev.modelarium.examples.schelling_segregation.entities.agents.attributes.segregation;

import dev.modelarium.examples.schelling_segregation.entities.agents.attributes.demographics.Group;
import dev.modelarium.examples.schelling_segregation.entities.agents.attributes.location.Cell;
import dev.modelarium.examples.schelling_segregation.spatial.SchellingSpatialUtils;
import modelarium.entities.agentsets.ReadOnlyAgentSet;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;

import java.util.function.Predicate;

/** Calculates and logs the fraction of an agent's occupied neighbours belonging to its own group. */
public final class SimilarNeighbourFractionProperty extends AgentProperty<Double> {
    private static final Predicate<modelarium.entities.readonly.ReadOnlyAgent> ALL_AGENTS = agent -> true;

    private final int width;
    private final int height;
    private double similarNeighbourFraction = 1.0;

    public SimilarNeighbourFractionProperty(int width, int height) {
        super("similar_neighbour_fraction", true, AttributeAccessLevel.PUBLIC, Double.class);
        this.width = width;
        this.height = height;
    }

    @Override
    protected void run(AgentContext context) {
        Group group = (Group) context.getThisEntity().getProperty("demographics", "group").get();
        Cell location = (Cell) context.getThisEntity().getProperty("location", "location").get();
        ReadOnlyAgentSet agents = context.getFilteredAgents(ALL_AGENTS);

        similarNeighbourFraction = SchellingSpatialUtils.similarNeighbourFraction(
                group,
                location,
                SchellingSpatialUtils.occupancy(agents),
                width,
                height
        );
    }

    @Override
    protected void set(AgentContext context, Double value) {
        similarNeighbourFraction = value;
    }

    @Override
    protected Double get(AgentContext context) {
        return similarNeighbourFraction;
    }
}
