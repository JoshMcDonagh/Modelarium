package modelarium.entities.attributes.sets.mutable;

import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.EnvironmentAttribute;
import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;

import java.util.List;

/**
 * Class for containing and managing a named group of an environment's attributes.
 *
 * <p>This class specialises {@link AttributeSet} so that retrieved attributes are returned as their
 * environment-flavoured types.
 */
public final class EnvironmentAttributeSet extends AttributeSet<EnvironmentSimulationContext, EnvironmentContext> {

    /**
     * Constructs a new environment attribute set with the specified owner, name and attributes.
     *
     * @param name the name of the attribute set, used to identify it within its owning environment
     * @param attributes the attributes the set will contain, in the order they will be run
     */
    public EnvironmentAttributeSet(String name, List<Attribute> attributes) {
        super(name, attributes);
    }

    /**
     * Retrieves a publicly accessible attribute by index.
     *
     * @param index the index of the attribute to retrieve
     * @return the {@link EnvironmentAttribute} at the specified index
     */
    public EnvironmentAttribute get(int index) {
        return (EnvironmentAttribute) super.get(index);
    }

    /**
     * Retrieves a publicly accessible attribute by name.
     *
     * @param attributeName the name of the attribute to retrieve
     * @return the {@link EnvironmentAttribute} with the specified name
     */
    public EnvironmentAttribute get(String attributeName) {
        return (EnvironmentAttribute) super.get(attributeName);
    }

    /**
     * Retrieves an event by its index among this set's events.
     *
     * @param eventIndex the index of the event to retrieve
     * @return the {@link EnvironmentEvent} at the specified index
     */
    public EnvironmentEvent getEvent(int eventIndex) {
        return (EnvironmentEvent) super.getEvent(eventIndex);
    }

    /**
     * Retrieves an event by name.
     *
     * @param eventName the name of the event to retrieve
     * @return the {@link EnvironmentEvent} with the specified name
     */
    public EnvironmentEvent getEvent(String eventName) {
        return (EnvironmentEvent) super.getEvent(eventName);
    }

    /**
     * Retrieves a routine by its index among this set's routines.
     *
     * @param routineIndex the index of the routine to retrieve
     * @return the {@link EnvironmentRoutine} at the specified index
     */
    public EnvironmentRoutine getRoutine(int routineIndex) {
        return (EnvironmentRoutine) super.getRoutine(routineIndex);
    }

    /**
     * Retrieves a routine by name.
     *
     * @param routineName the name of the routine to retrieve
     * @return the {@link EnvironmentRoutine} with the specified name
     */
    public EnvironmentRoutine getRoutine(String routineName) {
        return (EnvironmentRoutine) super.getRoutine(routineName);
    }

    /**
     * Retrieves a property by its index among this set's properties.
     *
     * @param propertyIndex the index of the property to retrieve
     * @return the {@link EnvironmentProperty} at the specified index
     */
    public EnvironmentProperty<?> getProperty(int propertyIndex) {
        return (EnvironmentProperty<?>) super.getProperty(propertyIndex);
    }

    /**
     * Retrieves a property by name.
     *
     * @param propertyName the name of the property to retrieve
     * @return the {@link EnvironmentProperty} with the specified name
     */
    public EnvironmentProperty<?> getProperty(String propertyName) {
        return (EnvironmentProperty<?>) super.getProperty(propertyName);
    }
}
