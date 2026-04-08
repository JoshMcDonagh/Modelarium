package modelarium.multithreading;

public record CoordinatorHandle(
        Thread coordinatorThread,
        CoordinatorThread coordinator
) {}
