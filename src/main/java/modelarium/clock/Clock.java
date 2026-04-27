package modelarium.clock;

public sealed interface Clock permits ImmutableClock, MutableClock {
    int currentTick();
    int totalTickCount();
    boolean isFinished();
}
