package modelarium.entities.attributes.events.functional;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.contexts.EnvironmentContext;

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
        return triggerLogic.isTriggered(context);
    }

    @Override
    public void run(EnvironmentContext context) {
        runLogic.run(context);
    }
}
