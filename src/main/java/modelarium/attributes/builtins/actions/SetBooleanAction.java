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

    public SetBooleanAction(String attributeSetName, String propertyName, boolean value) {
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
        this.value = value;
    }

    @Override
    public void apply(ModelElement element) {
        Property<Boolean> property = BuiltinLookup.getRequiredBooleanProperty(element, attributeSetName, propertyName);
        property.set(value);
    }
}
