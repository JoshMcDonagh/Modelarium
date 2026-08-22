package modelarium.entities.attributes.events;

import modelarium.entities.attributes.ReadOnlyAttribute;
import modelarium.utils.Cloners;

public final class ReadOnlyEvent extends ReadOnlyAttribute<Event<?>> {
    /**
     * Constructs a new immutable attribute wrapping the specified mutable attribute.
     *
     * @param attribute the mutable attribute to provide a read-only view of
     */
    ReadOnlyEvent(Event<?> attribute) {
        super(attribute);
    }

    /**
     * Returns whether this event's trigger condition is currently met.
     *
     * @return true if the event is triggered, false otherwise
     */
    public boolean isTriggered() {
        return Cloners.standard().deepClone(getMutableAttribute()).isTriggered();
    }
}
