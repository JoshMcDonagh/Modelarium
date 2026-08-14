package modelarium.entities;

import modelarium.entities.agents.Agent;
import modelarium.entities.attributes.sets.AttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.entities.logging.EntityLog;

/**
 * Interface for representing an element of the model that owns attribute sets.
 */
public sealed interface Entity permits Agent, Environment, MutableEntity, ImmutableEntity {
    /**
     * Returns the name of this entity.
     *
     * @return the entity's name
     */
    String name();

    /**
     * Returns the number of attribute sets this entity owns.
     *
     * @return the entity's attribute set count
     */
    int attributeSetCount();

    /**
     * Returns the total number of attributes across all of this entity's attribute sets.
     *
     * @return the entity's total attribute count
     */
    int attributeCount();

    /**
     * Retrieves an attribute set by index.
     *
     * @param attributeSetIndex the index of the attribute set to retrieve
     * @return the attribute set at the specified index
     */
    AttributeSet getAttributeSet(int attributeSetIndex);

    /**
     * Retrieves an attribute set by name.
     *
     * @param attributeSetName the name of the attribute set to retrieve
     * @return the attribute set with the specified name
     */
    AttributeSet getAttributeSet(String attributeSetName);

    /**
     * Returns the log of this entity's attribute values.
     *
     * @return a new {@link EntityLog} instance containing the logs of the entity's attribute sets
     */
    EntityLog<?,?,?,?> getLog();
}
