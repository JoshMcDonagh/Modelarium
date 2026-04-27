package modelarium.clock;

public final class ImmutableClock implements Clock {
    private final MutableClock clock;

    public ImmutableClock(MutableClock clock) {
        this.clock = clock;
    }

    @Override
    public int currentTick() {
        return clock.currentTick();
    }

    @Override
    public int totalTickCount() {
        return clock.totalTickCount();
    }

    @Override
    public boolean isFinished() {
        return clock.isFinished();
    }
}
