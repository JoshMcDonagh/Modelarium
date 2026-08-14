package modelarium.entities.attributes.properties;

import modelarium.entities.attributes.sets.mutable.AttributeBase;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.contexts.Context;

/**
 * Abstract class for representing an attribute that carries a typed value.
 *
 * <p>Each tick, a property's behaviour is run and (if the property is logged) its value is recorded. Properties can
 * also be read and written directly through their getter and setter. This class is extended by {@link AgentProperty}
 * and {@link EnvironmentProperty}.
 *
 * @param <T> the type of value this property carries
 * @param <C> the type of context this property is given
 */
public sealed abstract class Property<T,C extends Context> extends AttributeBase<C>
        permits AgentProperty, EnvironmentProperty {

    /** The class of the value this property carries */
    private final Class<T> type;

    /**
     * Constructs a new property with the specified name, logging flag, access level and value type.
     *
     * @param name the name of the property, used to identify it within its attribute set
     * @param isLogged whether the property's value is logged as the model progresses
     * @param accessLevel the access level of the property, determining whether other entities may read it
     * @param type the class of the value the property carries
     */
    Property(String name, boolean isLogged, AttributeAccessLevel accessLevel, Class<T> type) {
        super(name, isLogged, accessLevel);
        this.type = type;
    }

    /**
     * Returns the class of the value this property carries.
     *
     * @return the property's value type
     */
    public Class<T> type() {
        return type;
    }

    /**
     * Sets this property's value.
     *
     * @param value the value to set the property to
     */
    public void set(T value) {
        set(context(), value);
    }

    /**
     * Returns this property's current value.
     *
     * @return the property's value
     */
    public T get() {
        return get(context());
    }

    /**
     * Runs this property's behaviour for the current tick.
     */
    @Override
    public void run() {
        run(context());
    }

    /**
     * Runs this property's behaviour (does nothing by default). Can be overridden by subclasses.
     *
     * @param context the context the property can use in its behaviour
     */
    protected void run(C context) {}

    /**
     * Sets this property's value. Must be implemented by subclasses.
     *
     * @param context the context the property can use in its setter logic
     * @param value the value to set the property to
     */
    protected abstract void set(C context, T value);

    /**
     * Returns this property's current value. Must be implemented by subclasses.
     *
     * @param context the context the property can use in its getter logic
     * @return the property's value
     */
    protected abstract T get(C context);
}
