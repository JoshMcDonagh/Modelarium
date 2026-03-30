package modelarium.attributes.builtins.events;

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

    /**
     * Creates a timer event that triggers on a fixed periodic schedule.
     *
     * @param name the event name
     * @param isRecorded whether this event should be recorded in results
     * @param periodTicks the number of ticks between triggers; must be at least 1
     * @param offsetTicks the first tick at which the event may trigger; must be non-negative
     * @param actions the actions to apply when the event runs
     * @throws IllegalArgumentException if {@code periodTicks < 1} or
     * {@code offsetTicks < 0}
     */
    public TimerEvent(
            String name,
            boolean isRecorded,
            final int periodTicks,
            final int offsetTicks,
            final List<Action> actions
    ) {
        super(name, isRecorded);

        if (periodTicks < 1)
            throw new IllegalArgumentException("periodTicks must be >= 1");

        if (offsetTicks < 0)
            throw new IllegalArgumentException("offsetTicks must be >= 0");

        this.periodTicks = periodTicks;
        this.offsetTicks = offsetTicks;
        this.actions = List.copyOf(actions);
    }

    /**
     * Creates a timer event with an auto-generated name and an explicit recording flag.
     *
     * @param isRecorded whether this event should be recorded in results
     * @param periodTicks the number of ticks between triggers; must be at least 1
     * @param offsetTicks the first tick at which the event may trigger; must be non-negative
     * @param actions the actions to apply when the event runs
     * @throws IllegalArgumentException if {@code periodTicks < 1} or
     * {@code offsetTicks < 0}
     */
    public TimerEvent(
            boolean isRecorded,
            final int periodTicks,
            final int offsetTicks,
            final List<Action> actions
    ) {
        super(isRecorded);

        if (periodTicks < 1)
            throw new IllegalArgumentException("periodTicks must be >= 1");

        if (offsetTicks < 0)
            throw new IllegalArgumentException("offsetTicks must be >= 0");

        this.periodTicks = periodTicks;
        this.offsetTicks = offsetTicks;
        this.actions = List.copyOf(actions);
    }

    /**
     * Creates a timer event with an explicit name and default recording behaviour.
     *
     * @param name the event name
     * @param periodTicks the number of ticks between triggers; must be at least 1
     * @param offsetTicks the first tick at which the event may trigger; must be non-negative
     * @param actions the actions to apply when the event runs
     * @throws IllegalArgumentException if {@code periodTicks < 1} or
     * {@code offsetTicks < 0}
     */
    public TimerEvent(
            String name,
            final int periodTicks,
            final int offsetTicks,
            final List<Action> actions
    ) {
        super(name);

        if (periodTicks < 1)
            throw new IllegalArgumentException("periodTicks must be >= 1");

        if (offsetTicks < 0)
            throw new IllegalArgumentException("offsetTicks must be >= 0");

        this.periodTicks = periodTicks;
        this.offsetTicks = offsetTicks;
        this.actions = List.copyOf(actions);
    }

    /**
     * Creates a timer event with default name and recording behaviour.
     *
     * @param periodTicks the number of ticks between triggers; must be at least 1
     * @param offsetTicks the first tick at which the event may trigger; must be non-negative
     * @param actions the actions to apply when the event runs
     * @throws IllegalArgumentException if {@code periodTicks < 1} or
     * {@code offsetTicks < 0}
     */
    public TimerEvent(
            final int periodTicks,
            final int offsetTicks,
            final List<Action> actions
    ) {
        super();

        if (periodTicks < 1)
            throw new IllegalArgumentException("periodTicks must be >= 1");

        if (offsetTicks < 0)
            throw new IllegalArgumentException("offsetTicks must be >= 0");

        this.periodTicks = periodTicks;
        this.offsetTicks = offsetTicks;
        this.actions = List.copyOf(actions);
    }

    /**
     * Copy constructor.
     *
     * <p>Creates a new timer event with the same schedule and action list as the
     * supplied event.</p>
     *
     * @param other the event to copy
     */
    public TimerEvent(TimerEvent other) {
        super(other);
        this.periodTicks = other.periodTicks;
        this.offsetTicks = other.offsetTicks;
        this.actions = List.copyOf(other.actions);
    }

    /**
     * Returns whether this event should trigger on the current model tick.
     *
     * @return {@code true} if the current tick is on this event's schedule;
     * {@code false} otherwise
     */
    @Override
    public boolean isTriggered() {
        int tick = getAssociatedModelElement().getModelElementAccessor().getModelClock().getTick();
        return tick >= offsetTicks && ((tick - offsetTicks) % periodTicks == 0);
    }

    /**
     * Applies each configured action to the associated model element.
     */
    @Override
    public void run() {
        var element = getAssociatedModelElement();
        for (Action action : actions)
            action.apply(element);
    }
}
