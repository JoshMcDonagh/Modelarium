package modelarium.attributes.builtins.events;

import modelarium.attributes.Event;
import modelarium.attributes.builtins.actions.Action;

import java.util.List;

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
