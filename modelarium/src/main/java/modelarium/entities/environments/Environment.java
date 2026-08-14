package modelarium.entities.environments;

import modelarium.entities.Entity;
import modelarium.entities.attributes.sets.EnvironmentAttributeSet;
import modelarium.entities.attributes.sets.mutable.MutableEnvironmentAttributeSet;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.EntityLog;

/**
 * Interface for representing an environment in the model.
 */
public sealed interface Environment extends Entity permits MutableEnvironment, ImmutableEnvironment {
    /**
     * Returns the name of this environment.
     *
     * @return the environment's name
     */
    String name();

    /**
     * Returns the number of attribute sets this environment owns.
     *
     * @return the entity's attribute set count
     */
    int attributeSetCount();

    /**
     * Returns the total number of attributes across all of this environment's attribute sets.
     *
     * @return the environment's total attribute count
     */
    int attributeCount();

    /**
     * Retrieves an attribute set by index.
     *
     * @param attributeSetIndex the index of the attribute set to retrieve
     * @return the attribute set at the specified index
     */
    EnvironmentAttributeSet getAttributeSet(int attributeSetIndex);

    /**
     * Retrieves an attribute set by name.
     *
     * @param attributeSetName the name of the attribute set to retrieve
     * @return the attribute set with the specified name
     */
    EnvironmentAttributeSet getAttributeSet(String attributeSetName);

    /**
     * Returns the log of this environment's attribute values.
     *
     * @return a new {@link EntityLog} instance containing the logs of the environment's attribute sets
     */
    EntityLog<
            EnvironmentSimulationContext,
            EnvironmentContext,
            MutableEnvironmentAttributeSet,
            AttributeSetLog<EnvironmentSimulationContext>
            > getLog();
}
