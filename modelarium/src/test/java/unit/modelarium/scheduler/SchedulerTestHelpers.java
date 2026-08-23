package unit.modelarium.scheduler;

import modelarium.clock.ImmutableClock;
import modelarium.clock.MutableClock;
import modelarium.entities.environments.ReadOnlyEnvironment;

import static org.mockito.Mockito.mock;

public class SchedulerTestHelpers {
    private SchedulerTestHelpers() {}

    public static ImmutableClock immutableClock() {
        return new ImmutableClock(new MutableClock(10));
    }

    public static ReadOnlyEnvironment immutableEnvironment() {
        return mock(ReadOnlyEnvironment.class);
    }
}
