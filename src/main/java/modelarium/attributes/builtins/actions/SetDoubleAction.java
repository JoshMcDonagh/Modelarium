package modelarium.attributes.builtins.actions;

import modelarium.ModelElement;
import modelarium.attributes.Property;
import modelarium.attributes.builtins.refs.DoubleValueRef;
import modelarium.attributes.builtins.util.BuiltinLookup;

/**
 * An {@link Action} that sets a target {@code Double} property to a resolved value.
 *
 * <p>The target property is identified by attribute set name and property name. The new value is
 * obtained from a {@link modelarium.attributes.builtins.refs.DoubleValueRef}, allowing the action
 * to assign either a literal number or the current value of another property.</p>
 *
 * <p>This action is useful when an event should force a property to a specific value rather than
 * adjust it relative to its current value.</p>
 *
 * <p>The target property must exist, must be of type {@code Double}, and must support mutation.
 * If not, an exception is thrown at runtime.</p>
 */
public class SetDoubleAction implements Action {
    private final String attributeSetName;
    private final String propertyName;
    private final DoubleValueRef value;

    /**
     * Creates an action that assigns a resolved numeric value to a target property.
     *
     * @param attributeSetName the name of the attribute set containing the target property
     * @param propertyName the name of the target property
     * @param value the value reference supplying the value to assign
     */
    public SetDoubleAction(String attributeSetName, String propertyName, DoubleValueRef value) {
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
        this.value = value;
    }

    /**
     * Sets the target property on the supplied model element to the resolved value.
     *
     * @param element the model element whose property should be updated
     * @throws IllegalStateException if the target property cannot be found or is not a
     * {@code Double} property
     */
    @Override
    public void apply(ModelElement element) {
        Property<Double> property = BuiltinLookup.getRequiredDoubleProperty(element, attributeSetName, propertyName);
        property.set(value.resolve(element));
    }
}
