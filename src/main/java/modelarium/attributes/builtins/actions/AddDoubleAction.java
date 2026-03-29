package modelarium.attributes.builtins.actions;

import modelarium.ModelElement;
import modelarium.attributes.Property;
import modelarium.attributes.builtins.refs.DoubleValueRef;
import modelarium.attributes.builtins.util.BuiltinLookup;

/**
 * An {@link Action} that adds a resolved double delta to a target {@code Double} property.
 *
 * <p>The target property is identified by attribute set name and property name. The amount added
 * is obtained from a {@link modelarium.attributes.builtins.refs.DoubleValueRef}, which may resolve
 * to either a literal value or the current value of another property.</p>
 *
 * <p>This action is useful for simple event-driven increments such as increasing hunger, energy,
 * score, stock, or counters.</p>
 *
 * <p>The target property must exist, must be of type {@code Double}, and must be mutable. If not,
 * an exception is thrown at runtime.</p>
 */
public class AddDoubleAction implements Action {
    private final String attributeSetName;
    private final String propertyName;
    private final DoubleValueRef delta;

    /**
     * Creates an action that increments a target {@code Double} property by a
     * resolved delta.
     *
     * @param attributeSetName the name of the attribute set containing the target property
     * @param propertyName the name of the target property
     * @param delta the value reference supplying the amount to add
     */
    public AddDoubleAction(String attributeSetName, String propertyName, DoubleValueRef delta) {
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
        this.delta = delta;
    }

    /**
     * Adds the resolved delta to the target property on the supplied model element.
     *
     * @param element the model element whose property should be updated
     * @throws IllegalStateException if the target property cannot be found or is not a
     * {@code Double} property
     */
    @Override
    public void apply(ModelElement element) {
        Property<Double> property = BuiltinLookup.getRequiredDoubleProperty(element, attributeSetName, propertyName);
        property.set(property.get() + delta.resolve(element));
    }
}
