package modelarium.entities.attributes.functional.events;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.Event;
import modelarium.entities.contexts.Context;

/**
 * An event whose logic is defined via functional interfaces.
 *
 * <p>Useful for defining simple event logic without requiring full subclassing,
 * particularly when integrating with languages like Python.</p>
 */
public class FunctionalEvent extends Event {

    private final EventRunFunction runLogic;
    private final EventIsTriggeredFunction triggerLogic;

    public FunctionalEvent(String name, boolean isLogged, AttributeAccessLevel accessLevel, EventRunFunction runLogic, EventIsTriggeredFunction triggerLogic) {
        super(name, isLogged, accessLevel);
        this.runLogic = runLogic;
        this.triggerLogic = triggerLogic;
    }

    public FunctionalEvent(String name, EventRunFunction runLogic, EventIsTriggeredFunction triggerLogic) {
        super(name);
        this.runLogic = runLogic;
        this.triggerLogic = triggerLogic;
    }

    public FunctionalEvent(boolean isLogged, EventRunFunction runLogic, EventIsTriggeredFunction triggerLogic) {
        super(isLogged);
        this.runLogic = runLogic;
        this.triggerLogic = triggerLogic;
    }

    public FunctionalEvent(EventRunFunction runLogic, EventIsTriggeredFunction triggerLogic) {
        super();
        this.runLogic = runLogic;
        this.triggerLogic = triggerLogic;
    }

    public FunctionalEvent(String name, AttributeAccessLevel accessLevel, EventRunFunction runLogic, EventIsTriggeredFunction triggerLogic) {
        super(name, accessLevel);
        this.runLogic = runLogic;
        this.triggerLogic = triggerLogic;
    }

    public FunctionalEvent(boolean isLogged, AttributeAccessLevel accessLevel, EventRunFunction runLogic, EventIsTriggeredFunction triggerLogic) {
        super(isLogged, accessLevel);
        this.runLogic = runLogic;
        this.triggerLogic = triggerLogic;
    }

    public FunctionalEvent(AttributeAccessLevel accessLevel, EventRunFunction runLogic, EventIsTriggeredFunction triggerLogic) {
        super(accessLevel);
        this.runLogic = runLogic;
        this.triggerLogic = triggerLogic;
    }

    @Override
    protected boolean isTriggered(Context context) {
        return triggerLogic.isTriggered(context);
    }

    @Override
    protected void run(Context context) {
        runLogic.run(context);
    }
}
