package modelarium.entities.attributes.properties.functional.python;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.functional.EnvironmentPropertyGetterFunction;
import modelarium.entities.attributes.properties.functional.EnvironmentPropertyRunFunction;
import modelarium.entities.attributes.properties.functional.EnvironmentPropertySetterFunction;
import modelarium.entities.attributes.properties.functional.FunctionalEnvironmentProperty;

/**
 * Class for functional environment properties to be represented in Python.
 */
public class PythonFunctionalEnvironmentProperty<T>
        extends FunctionalEnvironmentProperty<T>
        implements PythonFunctionalProperty {
    private final String pythonType;

    /**
     * Constructs a new functional environment property with the specified logic functions.
     *
     * @param name        the name of the property, used to identify it within its attribute set
     * @param isLogged    whether the property's value is logged as the model progresses
     * @param accessLevel the access level of the property, determining whether other entities may read it
     * @param type        the class of the value the property carries
     * @param getter      the function defining the property's getter logic
     * @param setter      the function defining the property's setter logic
     * @param runLogic    the function defining the property's behaviour
     * @param pythonType  the stored python type
     */
    public PythonFunctionalEnvironmentProperty(
            String name,
            boolean isLogged,
            AttributeAccessLevel accessLevel,
            Class<T> type,
            EnvironmentPropertyGetterFunction<T> getter,
            EnvironmentPropertySetterFunction<T> setter,
            EnvironmentPropertyRunFunction<T> runLogic,
            String pythonType
    ) {
        super(name, isLogged, accessLevel, type, getter, setter, runLogic);
        this.pythonType = pythonType;
    }

    /**
     * Returns the stored python type.
     *
     * @return the stored python type as a {@link String}
     */
    @Override
    public String getPythonType() {
        return pythonType;
    }
}
