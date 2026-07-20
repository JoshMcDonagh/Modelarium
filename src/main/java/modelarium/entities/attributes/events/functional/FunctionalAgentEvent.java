package modelarium.entities.attributes.events.functional;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.contexts.AgentContext;
import modelarium.exceptions.MissingAttributeFunctionException;

/**
 * Class for representing an agent event whose logic is defined via functional interfaces.
 *
 * <p>This class is useful for defining simple event logic without requiring full subclassing, particularly when
 * integrating with languages like Python.
 */
public class FunctionalAgentEvent extends AgentEvent {

    /** The function defining this event's behaviour */
    private final AgentEventRunFunction runLogic;

    /** The function defining this event's trigger condition */
    private final AgentEventIsTriggeredFunction triggerLogic;

    /**
     * Constructs a new functional agent event with the specified logic functions.
     *
     * @param name the name of the event, used to identify it within its attribute set
     * @param isLogged whether the event's trigger state is logged as the model progresses
     * @param accessLevel the access level of the event, determining whether other entities may read it
     * @param runLogic the function defining the event's behaviour
     * @param triggerLogic the function defining the event's trigger condition
     */
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

    /**
     * Determines whether this event's trigger condition is met by applying the user-provided trigger function.
     *
     * @param context the context the event can use in its trigger logic
     * @return true if the event is triggered, false otherwise
     */
    @Override
    public boolean isTriggered(AgentContext context) {
        if (triggerLogic == null)
            throw new MissingAttributeFunctionException("No trigger logic function provided for '" + name() +"'");

        return triggerLogic.isTriggered(context);
    }

    /**
     * Runs this event's behaviour by applying the user-provided run function.
     *
     * @param context the context the event can use in its behaviour
     */
    @Override
    public void run(AgentContext context) {
        if (runLogic == null)
            throw new MissingAttributeFunctionException("No run logic function provided for '" + name() +"'");

        runLogic.run(context);
    }
}
