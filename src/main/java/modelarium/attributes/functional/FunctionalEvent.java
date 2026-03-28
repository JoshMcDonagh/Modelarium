package modelarium.attributes.functional;

import modelarium.attributes.Event;

/**
 * An event whose logic is defined via functional interfaces.
 *
 * <p>Useful for defining simple event logic without requiring full subclassing,
 * particularly when integrating with languages like Python.</p>
 */
public class FunctionalEvent extends Event {

    private final EventRunFunction runLogic;
    private final EventIsTriggeredFunction triggerLogic;

    /**
     * Constructs a functional event with the given name, recording flag, and behaviour.
     *
     * @param name the name of the event
     * @param isRecorded whether to record the event in output
     * @param runLogic logic to execute when {@link #run()} is called
     * @param triggerLogic logic to evaluate when {@link #isTriggered()} is called
     */
    public FunctionalEvent(String name, boolean isRecorded, EventRunFunction runLogic, EventIsTriggeredFunction triggerLogic) {
        super(name, isRecorded);
        this.runLogic = runLogic;
        this.triggerLogic = triggerLogic;
    }

    public FunctionalEvent(FunctionalEvent other) {
        super(other);
        this.runLogic = other.runLogic;
        this.triggerLogic = other.triggerLogic;
    }

    @Override
    public boolean isTriggered() {
        return triggerLogic.isTriggered(getAssociatedModelElement());
    }

    @Override
    public void run() {
        runLogic.run(getAssociatedModelElement());
    }

    @Override
    public FunctionalEvent deepCopy() {
        return new FunctionalEvent(this);
    }
}
