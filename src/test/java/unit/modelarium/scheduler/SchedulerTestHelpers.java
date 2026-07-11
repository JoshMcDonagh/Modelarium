package unit.modelarium.scheduler;

import modelarium.clock.ImmutableClock;
import modelarium.clock.MutableClock;
import modelarium.entities.immutable.ImmutableEnvironment;

import static org.mockito.Mockito.mock;

class SchedulerTestHelpers {
    private SchedulerTestHelpers() {}

    static ImmutableClock immutableClock() {
        return new ImmutableClock(new MutableClock(10));
    }

    static ImmutableEnvironment immutableEnvironment() {
        return mock(ImmutableEnvironment.class);
    }
}
