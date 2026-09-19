package modelarium.entities.attributes.properties;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.EnvironmentAttribute;
import modelarium.entities.contexts.EnvironmentContext;

/**
 * Abstract class for representing a property belonging to the environment.
 *
 * <p>Subclasses define the behaviour, setter and getter of the property using an {@link EnvironmentContext}.
 *
 * @param <T> the type of value this property carries
 */
public non-sealed abstract class EnvironmentProperty<T> extends Property<T, EnvironmentContext> implements EnvironmentAttribute {

    /**
     * Constructs a new environment property with the specified name, logging flag, access level and value type.
     *
     * @param name the name of the property, used to identify it within its attribute set
     * @param isLogged whether the property's value is logged as the model progresses
     * @param accessLevel the access level of the property, determining whether other entities may read it
     * @param type the class of the value the property carries
     */
    public EnvironmentProperty(String name, boolean isLogged, AttributeAccessLevel accessLevel, Class<T> type) {
        super(name, isLogged, accessLevel, type);
    }
}
