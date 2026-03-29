package modelarium.attributes.builtins.events;

import modelarium.attributes.Event;
import modelarium.attributes.builtins.actions.Action;

import java.util.List;

/**
 * An {@link modelarium.attributes.Event} that triggers on a fixed tick schedule.
 *
 * <p>A {@code TimerEvent} becomes triggered when the model clock tick is greater than or equal to
 * the configured offset and satisfies the configured period. When run, the event applies each of
 * its configured {@link modelarium.attributes.builtins.actions.Action Actions} to the associated
 * model element.</p>
 *
 * <p>This event is useful for recurring behaviours such as consuming resources every few ticks,
 * performing routine updates, or scheduling periodic state changes.</p>
 *
 * <p>For example, with {@code periodTicks = 10} and {@code offsetTicks = 3}, the event triggers
 * on ticks 3, 13, 23, and so on.</p>
 *
 * <p>{@code periodTicks} must be at least 1 and {@code offsetTicks} must be non-negative.</p>
 */
public class TimerEvent extends Event {
    private final int periodTicks;
    private final int offsetTicks;
    private final List<Action> actions;

    public TimerEvent(String name, boolean isRecorded, final int periodTicks, final int offsetTicks, final List<Action> actions) {
        super(name, isRecorded);

        if (periodTicks < 1)
            throw new IllegalArgumentException("periodTicks must be >= 1");

        if (offsetTicks < 0)
            throw new IllegalArgumentException("offsetTicks must be >= 0");

        this.periodTicks = periodTicks;
        this.offsetTicks = offsetTicks;
        this.actions = List.copyOf(actions);
    }

    @Override
    public boolean isTriggered() {
        int tick = getAssociatedModelElement().getModelElementAccessor().getModelClock().getTick();
        return tick >= offsetTicks && ((tick - offsetTicks) % periodTicks == 0);
    }

    @Override
    public void run() {
        var element = getAssociatedModelElement();
        for (Action action : actions)
            action.apply(element);
    }
}
