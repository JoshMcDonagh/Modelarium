package modelarium.entities;

import modelarium.entities.agents.immutable.ReadOnlyAgent;
import modelarium.entities.attributes.sets.immutable.ImmutableAttributeSet;
import modelarium.entities.attributes.sets.mutable.MutableAttributeSet;
import modelarium.entities.contexts.Context;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.environments.ReadOnlyEnvironment;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.EntityLog;

/**
 * Abstract class for providing a read-only view of an {@link Entity}.
 *
 * <p>This class wraps a mutable entity so that other model elements can inspect its name, attribute counts,
 * attributes and logs without being able to modify it. It is extended by {@link ReadOnlyAgent} and
 * {@link ReadOnlyEnvironment}.
 *
 * @param <SC> the type of simulation context the wrapped entity uses
 * @param <C> the type of context interface the wrapped entity's attributes are given
 * @param <AS> the type of attribute set the wrapped entity owns
 * @param <ASL> the type of attribute set log the wrapped entity produces
 */
public sealed abstract class ReadOnlyEntity<SC extends SimulationContext, C extends Context, AS extends MutableAttributeSet<SC,C>, ASL extends AttributeSetLog<SC>> permits ReadOnlyAgent, ReadOnlyEnvironment {

    /** The mutable entity this read-only view wraps */
    private final Entity<SC,C,AS,ASL> entity;

    /**
     * Constructs a new immutable entity wrapping the specified mutable entity.
     *
     * @param entity the mutable entity to provide a read-only view of
     */
    protected ReadOnlyEntity(Entity<SC,C,AS,ASL> entity) {
        this.entity = entity;
    }

    /**
     * Returns the mutable entity this read-only view wraps.
     *
     * @return the wrapped entity
     */
    protected Entity<SC,C,AS,ASL> getMutableEntity() {
        return entity;
    }

    /**
     * Returns the name of the wrapped entity.
     *
     * @return the entity's name
     */
    public String name() {
        return entity.name();
    }

    /**
     * Returns the number of attribute sets the wrapped entity owns.
     *
     * @return the entity's attribute set count
     */
    public int attributeSetCount() {
        return entity.attributeSetCount();
    }

    /**
     * Returns the total number of attributes across all of the wrapped entity's attribute sets.
     *
     * @return the entity's total attribute count
     */
    public int attributeCount() {
        return entity.attributeCount();
    }

    /**
     * Retrieves a read-only view of an attribute set by index. Must be implemented by subclasses.
     *
     * @param attributeSetIndex the index of the attribute set to retrieve
     * @return a read-only view of the attribute set at the specified index
     */
    public abstract ImmutableAttributeSet<SC,C> getAttributeSet(int attributeSetIndex);

    /**
     * Retrieves a read-only view of an attribute set by name. Must be implemented by subclasses.
     *
     * @param attributeSetName the name of the attribute set to retrieve
     * @return a read-only view of the attribute set with the specified name
     */
    public abstract ImmutableAttributeSet<SC,C> getAttributeSet(String attributeSetName);

    /**
     * Returns the log of the wrapped entity's attribute values.
     *
     * @return a new {@link EntityLog} instance containing the logs of the entity's attribute sets
     */
    public EntityLog<SC,C,AS,ASL> getLog() {
        return entity.getLog();
    }
}
