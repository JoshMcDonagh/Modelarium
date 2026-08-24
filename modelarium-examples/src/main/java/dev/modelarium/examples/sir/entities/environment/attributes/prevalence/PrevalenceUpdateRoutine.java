package dev.modelarium.examples.sir.entities.environment.attributes.prevalence;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.contexts.EnvironmentContext;

public class PrevalenceUpdateRoutine extends EnvironmentRoutine {
    private final Prevalence prevalence;

    public PrevalenceUpdateRoutine(Prevalence prevalence) {
        super("prevalence_update", AttributeAccessLevel.PRIVATE);
        this.prevalence = prevalence;
    }

    @Override
    protected void run(EnvironmentContext context) {
        prevalence.update(context);
    }
}
