package modelarium.entities.environments;

import modelarium.entities.ReadOnlyEntity;
import modelarium.entities.attributes.events.ReadOnlyEvent;
import modelarium.entities.attributes.properties.ReadOnlyProperty;
import modelarium.entities.attributes.routines.ReadOnlyRoutine;
import modelarium.entities.attributes.sets.immutable.ReadOnlyEnvironmentAttributeSet;
import modelarium.entities.attributes.sets.mutable.EnvironmentAttributeSet;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.logging.AttributeSetLog;

/**
 * Class for providing a read-only view of an {@link Environment}.
 *
 * <p>This class wraps the mutable environment so that other model elements can inspect it without being able to
 * modify it.
 */
public final class ReadOnlyEnvironment extends ReadOnlyEntity<EnvironmentSimulationContext, EnvironmentContext, EnvironmentAttributeSet, AttributeSetLog<EnvironmentSimulationContext>> {

    /**
     * Constructs a new immutable environment wrapping the specified mutable environment.
     *
     * @param entity the mutable environment to provide a read-only view of
     */
    public ReadOnlyEnvironment(Environment entity) {
        super(entity);
    }

    /**
     * Retrieves a read-only view of one of the wrapped environment's attribute sets by index.
     *
     * @param attributeSetIndex the index of the attribute set to retrieve
     * @return a new {@link ReadOnlyEnvironmentAttributeSet} instance wrapping the attribute set at the specified
     *         index
     */
    @Override
    public ReadOnlyEnvironmentAttributeSet getAttributeSet(int attributeSetIndex) {
        return new ReadOnlyEnvironmentAttributeSet(getMutableEntity().getAttributeSet(attributeSetIndex));
    }

    /**
     * Retrieves a read-only view of one of the wrapped environment's attribute sets by name.
     *
     * @param attributeSetName the name of the attribute set to retrieve
     * @return a new {@link ReadOnlyEnvironmentAttributeSet} instance wrapping the attribute set with the specified
     *         name
     */
    @Override
    public ReadOnlyEnvironmentAttributeSet getAttributeSet(String attributeSetName) {
        return new ReadOnlyEnvironmentAttributeSet(getMutableEntity().getAttributeSet(attributeSetName));
    }

    /**
     * Retrieves a read-only view of an event attribute from one of the wrapped environment's attribute sets.
     *
     * @param attributeSetName the name of the attribute set containing the event
     * @param eventName the name of the event to retrieve
     * @return the {@link ReadOnlyEvent} with the specified name
     */
    public ReadOnlyEvent getEvent(String attributeSetName, String eventName) {
        return getAttributeSet(attributeSetName).getEvent(eventName).getAsImmutable();
    }

    /**
     * Retrieves a read-only view of a routine attribute from one of the wrapped environment's attribute sets.
     *
     * @param attributeSetName the name of the attribute set containing the routine
     * @param routineName the name of the routine to retrieve
     * @return the {@link ReadOnlyRoutine} with the specified name
     */
    public ReadOnlyRoutine getRoutine(String attributeSetName, String routineName) {
        return getAttributeSet(attributeSetName).getRoutine(routineName).getAsImmutable();
    }

    /**
     * Retrieves a read-only view of a property attribute from one of the wrapped environment's attribute sets.
     *
     * @param attributeSetName the name of the attribute set containing the property
     * @param propertyName the name of the property to retrieve
     * @return the {@link ReadOnlyProperty} with the specified name
     */
    public ReadOnlyProperty<?> getProperty(String attributeSetName, String propertyName) {
        return getAttributeSet(attributeSetName).getProperty(propertyName).getAsImmutable();
    }
}
