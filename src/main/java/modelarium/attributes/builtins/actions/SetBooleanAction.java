package modelarium.attributes.builtins.actions;

import modelarium.ModelElement;
import modelarium.attributes.Property;
import modelarium.attributes.builtins.util.BuiltinLookup;

/**
 * An {@link Action} that sets a target {@code Boolean} property to a fixed value.
 *
 * <p>The target property is identified by attribute set name and property name. When applied, the
 * property is assigned the boolean value supplied at construction time.</p>
 *
 * <p>This action is useful for switching flags on or off in response to an event, such as setting
 * an agent to sleeping, infected, active, visible, or hungry.</p>
 *
 * <p>The target property must exist, must be of type {@code Boolean}, and must be mutable. If not,
 * an exception is thrown at runtime.</p>
 */
public class SetBooleanAction implements Action {
    private final String attributeSetName;
    private final String propertyName;
    private final boolean value;

    /**
     * Creates an action that assigns a fixed boolean value to a target property.
     *
     * @param attributeSetName the name of the attribute set containing the target property
     * @param propertyName the name of the target property
     * @param value the value to assign when the action is applied
     */
    public SetBooleanAction(String attributeSetName, String propertyName, boolean value) {
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
        this.value = value;
    }

    /**
     * Sets the target property on the supplied model element to this action's fixed value.
     *
     * @param element the model element whose property should be updated
     * @throws IllegalStateException if the target property cannot be found or is not a
     * {@code Boolean} property
     */
    @Override
    public void apply(ModelElement element) {
        Property<Boolean> property = BuiltinLookup.getRequiredBooleanProperty(element, attributeSetName, propertyName);
        property.set(value);
    }
}
