package modelarium.entities.attributes.properties.functional;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;
import modelarium.exceptions.MissingAttributeFunctionException;

/**
 * Class for representing an agent property whose behaviour is defined via functional interfaces.
 *
 * <p>This class allows dynamic configuration of property behaviour, which is useful when working with external
 * systems or when subclassing is not feasible (e.g. in Python via JPype). The property's value is stored internally
 * and passed to each of the user-provided functions.
 *
 * @param <T> the type of value this property carries
 */
public class FunctionalAgentProperty<T> extends AgentProperty<T> {

    /** The function defining this property's getter logic */
    private final AgentPropertyGetterFunction<T> getter;

    /** The function defining this property's setter logic */
    private final AgentPropertySetterFunction<T> setter;

    /** The function defining this property's behaviour */
    private final AgentPropertyRunFunction<T> runLogic;

    /** The value this property currently stores */
    private T propertyValue = null;

    /**
     * Constructs a new functional agent property with the specified logic functions.
     *
     * @param name the name of the property, used to identify it within its attribute set
     * @param isLogged whether the property's value is logged as the model progresses
     * @param accessLevel the access level of the property, determining whether other entities may read it
     * @param type the class of the value the property carries
     * @param getter the function defining the property's getter logic
     * @param setter the function defining the property's setter logic
     * @param runLogic the function defining the property's behaviour
     */
    public FunctionalAgentProperty(
            String name,
            boolean isLogged,
            AttributeAccessLevel accessLevel,
            Class<T> type,
            AgentPropertyGetterFunction<T> getter,
            AgentPropertySetterFunction<T> setter,
            AgentPropertyRunFunction<T> runLogic
    ) {
        super(name, isLogged, accessLevel, type);
        this.getter = getter;
        this.setter = setter;
        this.runLogic = runLogic;
    }

    /**
     * Runs this property's behaviour by applying the user-provided run function and storing its result.
     *
     * <p>If no run function was provided, this method does nothing.
     *
     * @param context the context the property can use in its behaviour
     */
    @Override
    public void run(AgentContext context) {
        if (runLogic == null)
            return; // Default run method is no-op for properties

        propertyValue = runLogic.run(context, propertyValue);
    }

    /**
     * Sets this property's value by applying the user-provided setter function and storing its result.
     *
     * @param context the context the property can use in its setter logic
     * @param value the value the property is being set to
     */
    @Override
    public void set(AgentContext context, T value) {
        if (setter == null)
            throw new MissingAttributeFunctionException("No setter function provided for '" + name() +"'");

        propertyValue = setter.set(context, propertyValue, value);
    }

    /**
     * Returns this property's value by applying the user-provided getter function.
     *
     * @param context the context the property can use in its getter logic
     * @return the value reported by the getter function
     */
    @Override
    public T get(AgentContext context) {
        if (getter == null)
            throw new MissingAttributeFunctionException("No getter function provided for '" + name() +"'");

        return getter.get(context, propertyValue);
    }
}
