package modelarium.entities.attributes.properties.functional;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;
import modelarium.exceptions.MissingAttributeFunctionException;

/**
 * A property whose behaviour is defined using functional interfaces.
 *
 * <p>This implementation allows dynamic configuration of property behaviour,
 * useful when working with external systems or when subclassing is not feasible
 * (e.g., in Python via JPype).</p>
 *
 * @param <T> the type of value this property holds
 */
public class FunctionalAgentProperty<T> extends AgentProperty<T> {
    private final AgentPropertyGetterFunction<T> getter;
    private final AgentPropertySetterFunction<T> setter;
    private final AgentPropertyRunFunction<T> runLogic;

    private T propertyValue = null;

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

    @Override
    public void run(AgentContext context) {
        if (runLogic == null)
            return; // Default run method is no-op for properties

        propertyValue = runLogic.run(context, propertyValue);
    }

    @Override
    public void set(AgentContext context, T value) {
        if (setter == null)
            throw new MissingAttributeFunctionException("No setter function provided for '" + name() +"'");

        propertyValue = setter.set(context, propertyValue, value);
    }

    @Override
    public T get(AgentContext context) {
        if (getter == null)
            throw new MissingAttributeFunctionException("No getter function provided for '" + name() +"'");

        return getter.get(context, propertyValue);
    }
}
