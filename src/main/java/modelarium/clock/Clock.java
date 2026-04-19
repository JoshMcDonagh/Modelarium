package modelarium.clock;

public sealed interface Clock permits SimulationClock {
    int currentTick();
    int totalTickCount();
    boolean isFinished();
}
