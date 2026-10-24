package modelarium.python.entities.attributes.properties.functional;

import modelarium.python.PythonObject;

/**
 * Interface for functional properties to be represented in Python.
 */
public interface PythonFunctionalProperty {
    /**
     * Returns the stored python type.
     *
     * @return the stored python type as a {@link String}
     */
    PythonObject getPythonType();
}
