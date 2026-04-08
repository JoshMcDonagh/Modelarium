package modelarium.entities.attributes.functional.properties;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.Property;
import modelarium.entities.contexts.Context;

/**
 * A property whose behaviour is defined using functional interfaces.
 *
 * <p>This implementation allows dynamic configuration of property behaviour,
 * useful when working with external systems or when subclassing is not feasible
 * (e.g., in Python via JPype).</p>
 *
 * @param <T> the type of value this property holds
 */
public class FunctionalProperty<T> extends Property<T> {

    private final PropertyGetterFunction<T> getter;
    private final PropertySetterFunction<T> setter;
    private final PropertyRunFunction<T> runLogic;

    private T propertyValue = null;

    public FunctionalProperty(
            String name,
            boolean isLogged,
            AttributeAccessLevel accessLevel,
            Class<T> type,
            PropertyGetterFunction<T> getter,
            PropertySetterFunction<T> setter,
            PropertyRunFunction<T> runLogic
    ) {
        super(name, isLogged, accessLevel, type);
        this.getter = getter;
        this.setter = setter;
        this.runLogic = runLogic;
    }

    public FunctionalProperty(
            String name,
            Class<T> type,
            PropertyGetterFunction<T> getter,
            PropertySetterFunction<T> setter,
            PropertyRunFunction<T> runLogic
    ) {
        super(name, type);
        this.getter = getter;
        this.setter = setter;
        this.runLogic = runLogic;
    }

    public FunctionalProperty(
            boolean isLogged,
            Class<T> type,
            PropertyGetterFunction<T> getter,
            PropertySetterFunction<T> setter,
            PropertyRunFunction<T> runLogic
    ) {
        super(isLogged, type);
        this.getter = getter;
        this.setter = setter;
        this.runLogic = runLogic;
    }

    public FunctionalProperty(
            Class<T> type,
            PropertyGetterFunction<T> getter,
            PropertySetterFunction<T> setter,
            PropertyRunFunction<T> runLogic
    ) {
        super(type);
        this.getter = getter;
        this.setter = setter;
        this.runLogic = runLogic;
    }

    public FunctionalProperty(
            String name,
            AttributeAccessLevel accessLevel,
            Class<T> type,
            PropertyGetterFunction<T> getter,
            PropertySetterFunction<T> setter,
            PropertyRunFunction<T> runLogic
    ) {
        super(name, accessLevel, type);
        this.getter = getter;
        this.setter = setter;
        this.runLogic = runLogic;
    }

    public FunctionalProperty(
            boolean isLogged,
            AttributeAccessLevel accessLevel,
            Class<T> type,
            PropertyGetterFunction<T> getter,
            PropertySetterFunction<T> setter,
            PropertyRunFunction<T> runLogic
    ) {
        super(isLogged, accessLevel, type);
        this.getter = getter;
        this.setter = setter;
        this.runLogic = runLogic;
    }

    public FunctionalProperty(
            AttributeAccessLevel accessLevel,
            Class<T> type,
            PropertyGetterFunction<T> getter,
            PropertySetterFunction<T> setter,
            PropertyRunFunction<T> runLogic
    ) {
        super(accessLevel, type);
        this.getter = getter;
        this.setter = setter;
        this.runLogic = runLogic;
    }

    public FunctionalProperty(
            String name,
            boolean isLogged,
            AttributeAccessLevel accessLevel,
            Class<T> type,
            PropertyGetterFunction<T> getter,
            PropertySetterFunction<T> setter
    ) {
        super(name, isLogged, accessLevel, type);
        this.getter = getter;
        this.setter = setter;
        this.runLogic = null;
    }

    public FunctionalProperty(
            String name,
            Class<T> type,
            PropertyGetterFunction<T> getter,
            PropertySetterFunction<T> setter
    ) {
        super(name, type);
        this.getter = getter;
        this.setter = setter;
        this.runLogic = null;
    }

    public FunctionalProperty(
            boolean isLogged,
            Class<T> type,
            PropertyGetterFunction<T> getter,
            PropertySetterFunction<T> setter
    ) {
        super(isLogged, type);
        this.getter = getter;
        this.setter = setter;
        this.runLogic = null;
    }

    public FunctionalProperty(
            Class<T> type,
            PropertyGetterFunction<T> getter,
            PropertySetterFunction<T> setter
    ) {
        super(type);
        this.getter = getter;
        this.setter = setter;
        this.runLogic = null;
    }

    public FunctionalProperty(
            String name,
            AttributeAccessLevel accessLevel,
            Class<T> type,
            PropertyGetterFunction<T> getter,
            PropertySetterFunction<T> setter
    ) {
        super(name, accessLevel, type);
        this.getter = getter;
        this.setter = setter;
        this.runLogic = null;
    }

    public FunctionalProperty(
            boolean isLogged,
            AttributeAccessLevel accessLevel,
            Class<T> type,
            PropertyGetterFunction<T> getter,
            PropertySetterFunction<T> setter
    ) {
        super(isLogged, accessLevel, type);
        this.getter = getter;
        this.setter = setter;
        this.runLogic = null;
    }

    public FunctionalProperty(
            AttributeAccessLevel accessLevel,
            Class<T> type,
            PropertyGetterFunction<T> getter,
            PropertySetterFunction<T> setter
    ) {
        super(accessLevel, type);
        this.getter = getter;
        this.setter = setter;
        this.runLogic = null;
    }

    @Override
    protected void run(Context context) {
        if (runLogic == null)
            return; // Default run method is no-op for properties

        propertyValue = runLogic.run(context, propertyValue);
    }

    @Override
    protected void set(Context context, T value) {
        propertyValue = setter.set(context, propertyValue, value);
    }

    @Override
    protected T get(Context context) {
        return getter.get(context, propertyValue);
    }
}
