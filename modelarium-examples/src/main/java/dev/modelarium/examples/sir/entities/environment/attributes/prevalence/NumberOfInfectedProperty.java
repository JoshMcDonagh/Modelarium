package dev.modelarium.examples.sir.entities.environment.attributes.prevalence;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.EnvironmentContext;

public class NumberOfInfectedProperty extends EnvironmentProperty<Integer> {
    private final Prevalence prevalence;

    public NumberOfInfectedProperty(Prevalence prevalence) {
        super("number_of_infected", true, AttributeAccessLevel.PRIVATE, Integer.class);
        this.prevalence = prevalence;
    }

    @Override
    protected void set(EnvironmentContext context, Integer value) {
        throw new UnsupportedOperationException("Number of infected property should not be set externally");
    }

    @Override
    protected Integer get(EnvironmentContext context) {
        return prevalence.numberOfInfected();
    }
}
