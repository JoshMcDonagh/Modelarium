package dev.modelarium.examples.el_farol_bar.entities.agents.prediction;

import java.util.ArrayList;
import java.util.List;

/**
 * The sequence of publicly observed weekly attendance figures remembered by an agent.
 */
public final class AttendanceHistory {
    private final List<Integer> attendances;

    public AttendanceHistory(List<Integer> initialAttendances) {
        if (initialAttendances == null || initialAttendances.isEmpty())
            throw new IllegalArgumentException("Initial attendance history must contain at least one value");

        attendances = new ArrayList<>(initialAttendances);
    }

    public int size() {
        return attendances.size();
    }

    /** Returns the most recently observed attendance. */
    public int latest() {
        return getFromEnd(1);
    }

    /**
     * Returns an attendance relative to the end of the history: 1 is last week, 2 is two weeks ago, and so on.
     */
    public int getFromEnd(int weeksAgo) {
        if (weeksAgo < 1 || weeksAgo > attendances.size())
            throw new IllegalArgumentException("weeksAgo must be between 1 and " + attendances.size());

        return attendances.get(attendances.size() - weeksAgo);
    }

    /** Returns the most recent {@code count} attendance values, oldest first. */
    public List<Integer> last(int count) {
        if (count < 1 || count > attendances.size())
            throw new IllegalArgumentException("count must be between 1 and " + attendances.size());

        return List.copyOf(attendances.subList(attendances.size() - count, attendances.size()));
    }

    public void observe(int attendance) {
        attendances.add(attendance);
    }

    @Override
    public String toString() {
        return attendances.toString();
    }
}
