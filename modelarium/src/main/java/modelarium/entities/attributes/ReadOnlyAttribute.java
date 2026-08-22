package modelarium.entities.attributes;

import modelarium.entities.attributes.events.ReadOnlyEvent;
import modelarium.entities.attributes.properties.ReadOnlyProperty;
import modelarium.entities.attributes.routines.ReadOnlyRoutine;

/**
 * Class for providing a read-only view of an {@link Attribute}
 *
 * <p>This class wraps a mutable attribute so that other model elements can inspect it without being able to modify it.
 */
public sealed abstract class ReadOnlyAttribute<T extends Attribute> permits ReadOnlyEvent, ReadOnlyProperty, ReadOnlyRoutine {
    /** The mutable attribute this read-only view wraps */
    private final T attribute;

    /**
     * Constructs a new immutable attribute wrapping the specified mutable attribute.
     *
     * @param attribute the mutable attribute to provide a read-only view of
     */
    protected ReadOnlyAttribute(T attribute) {
        this.attribute = attribute;
    }

    protected T getMutableAttribute() {
        return attribute;
    }

    /**
     * Returns the name of this attribute.
     *
     * @return the attribute's name
     */
    public String name() {
        return attribute.name();
    }

    /**
     * Returns whether this attribute's state is logged as the model progresses.
     *
     * @return true if the attribute is logged, false otherwise
     */
    public boolean isLogged() {
        return attribute.isLogged();
    }

    /**
     * Returns the access level of this attribute, determining whether other entities may read it.
     *
     * @return the attribute's {@link AttributeAccessLevel}
     */
    public AttributeAccessLevel accessLevel() {
        return attribute.accessLevel();
    }
}
