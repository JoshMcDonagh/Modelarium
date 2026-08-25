package modelarium.entities.attributes;

import modelarium.entities.attributes.events.Event;
import modelarium.entities.attributes.properties.Property;
import modelarium.entities.attributes.routines.Routine;
import modelarium.entities.contexts.Context;
import modelarium.internal.Internal;

/**
 * Abstract class for providing the shared state and behaviour of all attribute types.
 *
 * <p>This class is responsible for storing an attribute's name, access level and logging flag, along with the
 * context the attribute uses to interact with the rest of the model. It is extended by {@link Property},
 * {@link Event} and {@link Routine}.
 *
 * @param <C> the type of context this attribute is given
 */
public non-sealed abstract class AttributeBase<C extends Context> implements Attribute {

    /** The name of this attribute, used to identify it within its attribute set */
    private final String name;

    /** Whether this attribute's state is logged as the model progresses */
    private final boolean isLogged;

    /** The access level of this attribute, determining whether other entities may read it */
    private final AttributeAccessLevel accessLevel;

    /** The context this attribute uses to interact with the rest of the model, set once by its attribute set */
    private C context = null;

    /**
     * Constructs a new attribute with the specified name, logging flag and access level.
     *
     * @param name the name of the attribute, used to identify it within its attribute set
     * @param isLogged whether the attribute's state is logged as the model progresses
     * @param accessLevel the access level of the attribute, determining whether other entities may read it
     */
    public AttributeBase(String name, boolean isLogged, AttributeAccessLevel accessLevel) {
        this.name = name;
        this.isLogged = isLogged;
        this.accessLevel = accessLevel;
    }

    /**
     * Returns the name of this attribute.
     *
     * @return the attribute's name
     */
    public String name() {
        return name;
    }

    /**
     * Returns whether this attribute's state is logged as the model progresses.
     *
     * @return true if the attribute is logged, false otherwise
     */
    public boolean isLogged() {
        return isLogged;
    }

    /**
     * Returns the context this attribute uses to interact with the rest of the model.
     *
     * @return the attribute's context, or null if it has not yet been set
     */
    protected C context() {
        return context;
    }

    /**
     * Provides this attribute with the context it will use to interact with the rest of the model.
     *
     * @param context the context to provide the attribute with
     */
    @Internal
    public void setContext(C context) {
        if (this.context != null)
            throw new IllegalStateException("Context already set");

        this.context = context;
    }

    /**
     * Returns the access level of this attribute, determining whether other entities may read it.
     *
     * @return the attribute's {@link AttributeAccessLevel}
     */
    public AttributeAccessLevel accessLevel() {
        return accessLevel;
    }
}
