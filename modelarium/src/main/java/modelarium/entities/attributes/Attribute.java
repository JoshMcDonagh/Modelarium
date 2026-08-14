package modelarium.entities.attributes;

import modelarium.entities.attributes.sets.mutable.AttributeBase;

/**
 * Interface for representing a single named behaviour or value belonging to an entity.
 *
 * <p>Attributes are the units of behaviour the model runs each tick, and come in three forms: events, routines and
 * properties. Each attribute has a name, an access level, and a flag indicating whether its state is logged as the
 * model progresses.
 */
public sealed interface Attribute permits AgentAttribute, AttributeBase, EnvironmentAttribute {

    /**
     * Returns the name of this attribute.
     *
     * @return the attribute's name
     */
    String name();

    /**
     * Returns whether this attribute's state is logged as the model progresses.
     *
     * @return true if the attribute is logged, false otherwise
     */
    boolean isLogged();

    /**
     * Returns the access level of this attribute, determining whether other entities may read it.
     *
     * @return the attribute's {@link AttributeAccessLevel}
     */
    AttributeAccessLevel accessLevel();

    /**
     * Runs this attribute's behaviour for the current tick.
     */
    void run();
}
