package modelarium.attributes;

import java.util.List;

/**
 * A concrete collection class for managing {@link Event} attributes.
 *
 * <p>Each event is uniquely named and stored internally. During each simulation tick,
 * the {@link #run()} method checks whether each event is triggered before executing its logic.
 *
 * <p>This class extends {@link Attributes} and uses the underlying storage and indexing mechanisms
 * provided there.
 */
public class Events extends Attributes {

    /**
     * Adds a single event to the collection.
     *
     * @param event the event to add
     */
    public void add(Event event) {
        addAttribute(event);
    }

    /**
     * Adds a list of events to the collection.
     *
     * @param events a list of {@link Event} instances
     */
    public void add(List<Event> events) {
        for (Event event : events)
            add(event);
    }

    /**
     * Retrieves an event by name.
     *
     * @param name the name of the event
     * @return the matching {@link Event}, or null if not found
     */
    public Event get(String name) {
        return (Event) getAttribute(name);
    }

    /**
     * Retrieves an event by index.
     *
     * @param index the index of the event
     * @return the {@link Event} at the given index
     */
    public Event get(int index) {
        return (Event) getAttribute(index);
    }

    /**
     * Executes all triggered events in the collection.
     *
     * <p>Each event is first checked with {@code isTriggered()} before being run.
     * This allows events to conditionally execute based on internal or external logic.
     */
    @Override
    public void run() {
        for (int i = 0; i < size(); i++) {
            Event event = get(i);
            if (event.isTriggered())
                event.run();
        }
    }

    @Override
    public Events deepCopy() {
        Events eventsCopy = new Events();
        for (int i = 0; i < size(); i++)
            eventsCopy.add(get(i).deepCopy());
        return eventsCopy;
    }

    /**
     * Checks if an event of a given name exists.
     *
     * @param name  the name of the event to check for
     * @return  the boolean value reflecting whether the event exists or not
     */
    public boolean contains(String name) {
        return hasAttribute(name);
    }
}
