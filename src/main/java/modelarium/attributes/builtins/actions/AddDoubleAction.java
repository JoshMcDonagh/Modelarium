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
 *
 * <p>Example usage:</p>
 * <pre>
 * Action increaseEnergy = new AddDoubleAction(
 *         "state",
 *         "energy",
 *         new LiteralDoubleRef(5.0)
 * );
 * </pre>
 */
public class AddDoubleAction implements Action {
    private final String attributeSetName;
    private final String propertyName;
    private final DoubleValueRef delta;

    public AddDoubleAction(String attributeSetName, String propertyName, DoubleValueRef delta) {
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
        this.delta = delta;
    }

    @Override
    public void apply(ModelElement element) {
        Property<Double> property = BuiltinLookup.getRequiredDoubleProperty(element, attributeSetName, propertyName);
        property.set(property.get() + delta.resolve(element));
    }
}
