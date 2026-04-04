package modelarium.entities.attributes.functional.events;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.Event;

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
    public boolean isTriggered() {
        return triggerLogic.isTriggered(owner());
    }

    @Override
    public void run() {
        runLogic.run(owner());
    }
}
