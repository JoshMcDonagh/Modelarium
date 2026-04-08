package modelarium.entities.attributes.properties.functional;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.EnvironmentContext;

public class FunctionalEnvironmentProperty<T> extends EnvironmentProperty<T> {
    private final EnvironmentPropertyGetterFunction<T> getter;
    private final EnvironmentPropertySetterFunction<T> setter;
    private final EnvironmentPropertyRunFunction<T> runLogic;

    private T propertyValue = null;

    public FunctionalEnvironmentProperty(
            String name,
            boolean isLogged,
            AttributeAccessLevel accessLevel,
            Class<T> type,
            EnvironmentPropertyGetterFunction<T> getter,
            EnvironmentPropertySetterFunction<T> setter,
            EnvironmentPropertyRunFunction<T> runLogic
    ) {
        super(name, isLogged, accessLevel, type);
        this.getter = getter;
        this.setter = setter;
        this.runLogic = runLogic;
    }

    @Override
    public void run(EnvironmentContext context) {
        if (runLogic == null)
            return; // Default run method is no-op for properties

        propertyValue = runLogic.run(context, propertyValue);
    }

    @Override
    public void set(EnvironmentContext context, T value) {
        propertyValue = setter.set(context, propertyValue, value);
    }

    @Override
    public T get(EnvironmentContext context) {
        return getter.get(context, propertyValue);
    }
}
