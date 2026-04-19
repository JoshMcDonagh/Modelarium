package modelarium.entities.attributes.events.functional;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.contexts.AgentContext;
import modelarium.exceptions.MissingAttributeFunctionException;

/**
 * An event whose logic is defined via functional interfaces.
 *
 * <p>Useful for defining simple event logic without requiring full subclassing,
 * particularly when integrating with languages like Python.</p>
 */
public class FunctionalAgentEvent extends AgentEvent {
    private final AgentEventRunFunction runLogic;
    private final AgentEventIsTriggeredFunction triggerLogic;

    public FunctionalAgentEvent(
            String name,
            boolean isLogged,
            AttributeAccessLevel accessLevel,
            AgentEventRunFunction runLogic,
            AgentEventIsTriggeredFunction triggerLogic
    ) {
        super(name, isLogged, accessLevel);
        this.runLogic = runLogic;
        this.triggerLogic = triggerLogic;
    }

    @Override
    public boolean isTriggered(AgentContext context) {
        if (triggerLogic == null)
            throw new MissingAttributeFunctionException("No trigger logic function provided for '" + name() +"'");

        return triggerLogic.isTriggered(context);
    }

    @Override
    public void run(AgentContext context) {
        if (runLogic == null)
            throw new MissingAttributeFunctionException("No run logic function provided for '" + name() +"'");

        runLogic.run(context);
    }
}
