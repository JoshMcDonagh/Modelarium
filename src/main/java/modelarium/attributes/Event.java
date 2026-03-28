package modelarium.attributes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Represents a boolean-triggered event that is evaluated each tick.
 *
 * <p>Events can encapsulate internal logic or external stimuli, and can be named explicitly or assigned
 * a unique name automatically. Each event increases the global event count.
 */
public abstract class Event extends Attribute {

    /** Global counter to assign default event names */
    private static int eventCount = 0;

    /**
     * Constructs an event with a specific name and recording flag.
     *
     * @param name the event name
     * @param isRecorded whether the event is recorded in output
     */
    public Event(String name, boolean isRecorded) {
        super(name != null ? name : "Event " + eventCount, isRecorded);
        eventCount++;
    }

    /**
     * Constructs an event with a generated name and a recording flag.
     *
     * @param isRecorded whether the event is recorded
     */
    public Event(boolean isRecorded) {
        this("Event " + eventCount++, isRecorded);
    }

    /**
     * Constructs an event with a specified name, defaulting to recording enabled.
     *
     * @param name the event name
     */
    public Event(String name) {
        this(name, true);
    }

    /**
     * Constructs an event with a generated name and recording enabled by default.
     */
    public Event() {
        this("Event " + eventCount++, true);
    }

    /**
     * Constructs a deep copy event from a given event.
     *
     * @param other the event to deep copy
     */
    public Event(Event other) {
        super(other);
    }

    /**
     * Indicates whether the event has been triggered during this tick.
     *
     * @return true if triggered
     */
    public abstract boolean isTriggered();

    /**
     * Runs this event's internal logic.
     */
    @Override
    public abstract void run();

    @Override
    public Event deepCopy() {
        try {
            // Get the concrete class
            Class<? extends Event> clazz = this.getClass();

            // Use copy constructor if defined
            try {
                Constructor<? extends Event> copyCtor = clazz.getDeclaredConstructor(clazz);
                copyCtor.setAccessible(true);
                return copyCtor.newInstance(this);
            } catch (NoSuchMethodException ignored) {}

            // Otherwise, try no-arg constructor
            Constructor<? extends Event> ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            Event copy = ctor.newInstance();

            // Use reflection to copy fields (shallow or deep depending on type)
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(this);
                field.set(copy, value); // shallow copy
            }

            return copy;

        } catch (Exception e) {
            throw new RuntimeException("Failed to deep copy Event of type " + this.getClass().getName(), e);
        }
    }
}
