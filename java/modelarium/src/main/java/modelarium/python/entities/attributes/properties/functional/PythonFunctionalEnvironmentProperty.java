package modelarium.python.entities.attributes.properties.functional;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.functional.EnvironmentPropertyGetterFunction;
import modelarium.entities.attributes.properties.functional.EnvironmentPropertyRunFunction;
import modelarium.entities.attributes.properties.functional.EnvironmentPropertySetterFunction;
import modelarium.entities.attributes.properties.functional.FunctionalEnvironmentProperty;
import modelarium.python.PythonObject;
import modelarium.python.PythonValue;

/**
 * Class for functional environment properties to be represented in Python.
 */
public class PythonFunctionalEnvironmentProperty
        extends FunctionalEnvironmentProperty<PythonValue>
        implements PythonFunctionalProperty {
    private final PythonObject pythonType;

    /**
     * Constructs a new functional environment property with the specified logic functions.
     *
     * @param name        the name of the property, used to identify it within its attribute set
     * @param isLogged    whether the property's value is logged as the model progresses
     * @param accessLevel the access level of the property, determining whether other entities may read it
     * @param getter      the function defining the property's getter logic
     * @param setter      the function defining the property's setter logic
     * @param runLogic    the function defining the property's behaviour
     * @param pythonType  the stored python type
     */
    public PythonFunctionalEnvironmentProperty(
            String name,
            boolean isLogged,
            AttributeAccessLevel accessLevel,
            EnvironmentPropertyGetterFunction<PythonValue> getter,
            EnvironmentPropertySetterFunction<PythonValue> setter,
            EnvironmentPropertyRunFunction<PythonValue> runLogic,
            PythonObject pythonType
    ) {
        super(name, isLogged, accessLevel, PythonValue.class, getter, setter, runLogic);
        this.pythonType = pythonType;
    }

    /**
     * Returns the stored python type.
     *
     * @return the stored python type as a {@link String}
     */
    @Override
    public PythonObject getPythonType() {
        return pythonType;
    }
}
