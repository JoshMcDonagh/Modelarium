package modelarium.entities.attributes.events.functional;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.exceptions.MissingAttributeFunctionException;

public class FunctionalEnvironmentEvent extends EnvironmentEvent {
    private final EnvironmentEventRunFunction runLogic;
    private final EnvironmentEventIsTriggeredFunction triggerLogic;

    public FunctionalEnvironmentEvent(
            String name,
            boolean isLogged,
            AttributeAccessLevel accessLevel,
            EnvironmentEventRunFunction runLogic,
            EnvironmentEventIsTriggeredFunction triggerLogic
    ) {
        super(name, isLogged, accessLevel);
        this.runLogic = runLogic;
        this.triggerLogic = triggerLogic;
    }

    @Override
    public boolean isTriggered(EnvironmentContext context) {
        if (triggerLogic == null)
            throw new MissingAttributeFunctionException("No trigger logic function provided for '" + name() +"'");

        return triggerLogic.isTriggered(context);
    }

    @Override
    public void run(EnvironmentContext context) {
        if (runLogic == null)
            throw new MissingAttributeFunctionException("No run logic function provided for '" + name() +"'");

        runLogic.run(context);
    }
}
