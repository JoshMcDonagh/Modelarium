package dev.modelarium.examples.schelling_segregation.entities.agents.attributes.segregation;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;

/** Records whether the agent currently meets the configured similarity threshold. */
public final class SatisfiedProperty extends AgentProperty<Boolean> {
    private final double minimumSimilarNeighbourFraction;
    private boolean satisfied = true;

    public SatisfiedProperty(double minimumSimilarNeighbourFraction) {
        super("satisfied", true, AttributeAccessLevel.PUBLIC, Boolean.class);
        this.minimumSimilarNeighbourFraction = minimumSimilarNeighbourFraction;
    }

    @Override
    protected void run(AgentContext context) {
        double similarNeighbourFraction = (Double) context
                .getThisEntity()
                .getProperty("segregation", "similar_neighbour_fraction")
                .get();

        satisfied = similarNeighbourFraction >= minimumSimilarNeighbourFraction;
    }

    @Override
    protected void set(AgentContext context, Boolean value) {
        satisfied = value;
    }

    @Override
    protected Boolean get(AgentContext context) {
        return satisfied;
    }
}
