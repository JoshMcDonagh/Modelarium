package dev.modelarium.examples.sir.entities.environment.attributes.prevalence;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.EnvironmentContext;

public class InfectedPercentageProperty extends EnvironmentProperty<Double> {
    private final Prevalence prevalence;

    public InfectedPercentageProperty(Prevalence prevalence) {
        super("infected_percentage", true, AttributeAccessLevel.PRIVATE, Double.class);
        this.prevalence = prevalence;
    }
    @Override
    protected void set(EnvironmentContext context, Double value) {
        throw new UnsupportedOperationException("Infected percentage property should not be set externally");
    }

    @Override
    protected Double get(EnvironmentContext context) {
        return prevalence.infectedPercentage();
    }

}
