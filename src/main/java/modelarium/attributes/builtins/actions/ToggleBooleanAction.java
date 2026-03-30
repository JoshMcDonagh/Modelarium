package modelarium.attributes.builtins.actions;

import modelarium.Entity;
import modelarium.attributes.builtins.util.BuiltinLookup;

/**
 * An {@link Action} that inverts the current value of a target {@code Boolean} property.
 *
 * <p>The target property is identified by attribute set name and property name. When applied, the
 * current boolean value is read and then replaced with its logical negation.</p>
 *
 * <p>This action is useful for simple state flips such as toggling sleeping/awake, enabled/disabled,
 * or open/closed flags.</p>
 *
 * <p>The target property must exist, must be of type {@code Boolean}, and must be mutable. If not,
 * an exception is thrown at runtime.</p>
 */
public class ToggleBooleanAction implements Action {
    private final String attributeSetName;
    private final String propertyName;

    /**
     * Creates an action that toggles a target boolean property.
     *
     * @param attributeSetName the name of the attribute set containing the target property
     * @param propertyName the name of the target property
     */
    public ToggleBooleanAction(String attributeSetName, String propertyName) {
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
    }

    /**
     * Toggles the current value of the target property on the supplied model element.
     *
     * @param element the model element whose property should be updated
     * @throws IllegalStateException if the target property cannot be found or is not a
     * {@code Boolean} property
     */
    @Override
    public void apply(Entity element) {
        Property<Boolean> property = BuiltinLookup.getRequiredBooleanProperty(element, attributeSetName, propertyName);
        property.set(!property.get());
    }
}
