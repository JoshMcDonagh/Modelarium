package unit.modelarium.scheduler;

import modelarium.clock.ReadOnlyClock;
import modelarium.clock.Clock;
import modelarium.entities.readonly.ReadOnlyEnvironment;

import static org.mockito.Mockito.mock;

public class SchedulerTestHelpers {
    private SchedulerTestHelpers() {}

    public static ReadOnlyClock immutableClock() {
        return new ReadOnlyClock(new Clock(10));
    }

    public static ReadOnlyEnvironment immutableEnvironment() {
        return mock(ReadOnlyEnvironment.class);
    }
}
