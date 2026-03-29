package modelarium.attributes.builtins.events;

import modelarium.attributes.Event;
import modelarium.attributes.builtins.actions.Action;
import modelarium.attributes.builtins.refs.DoubleValueRef;
import modelarium.attributes.builtins.util.BuiltinLookup;

import java.util.List;

/**
 * An {@link modelarium.attributes.Event} that triggers when a {@code Double} property crosses a
 * threshold in a specified direction.
 *
 * <p>The monitored property is identified by attribute set name and property name. The threshold is
 * provided by a {@link modelarium.attributes.builtins.refs.DoubleValueRef}, so it may be either a
 * fixed value or a value obtained dynamically from another property.</p>
 *
 * <p>This event detects crossings rather than simple threshold membership. In other words, it only
 * triggers when the monitored value moves from one side of the threshold to the other in the chosen
 * direction. It does not repeatedly trigger on every tick while the value remains above or below
 * the threshold.</p>
 *
 * <p>The first call to {@link #isTriggered()} establishes the initial previous value and does not
 * trigger a crossing by itself.</p>
 *
 * <p>Typical uses include detecting when an agent becomes hungry, when stock falls below a minimum,
 * or when a physiological value rises above or falls below a critical threshold.</p>
 */
public class ThresholdCrossingEvent extends Event {
    private final String attributeSetName;
    private final String propertyName;
    private final DoubleValueRef threshold;
    private final ThresholdDirection direction;
    private final List<Action> actions;

    private Double previousValue = null;

    public ThresholdCrossingEvent(
            String name,
            boolean isRecorded,
            String attributeSetName,
            String propertyName,
            DoubleValueRef threshold,
            ThresholdDirection direction,
            List<Action> actions
    ) {
        super(name, isRecorded);
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
        this.threshold = threshold;
        this.direction = direction;
        this.actions = actions;
    }

    public ThresholdCrossingEvent(
            boolean isRecorded,
            String attributeSetName,
            String propertyName,
            DoubleValueRef threshold,
            ThresholdDirection direction,
            List<Action> actions
    ) {
        super(isRecorded);
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
        this.threshold = threshold;
        this.direction = direction;
        this.actions = actions;
    }

    public ThresholdCrossingEvent(
            String name,
            String attributeSetName,
            String propertyName,
            DoubleValueRef threshold,
            ThresholdDirection direction,
            List<Action> actions
    ) {
        super(name);
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
        this.threshold = threshold;
        this.direction = direction;
        this.actions = actions;
    }

    public ThresholdCrossingEvent(
            String attributeSetName,
            String propertyName,
            DoubleValueRef threshold,
            ThresholdDirection direction,
            List<Action> actions
    ) {
        super();
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
        this.threshold = threshold;
        this.direction = direction;
        this.actions = actions;
    }

    @Override
    public boolean isTriggered() {
        double currentValue = BuiltinLookup.getRequiredDoublePropertyValue(
                getAssociatedModelElement(),
                attributeSetName,
                propertyName
        );

        double thresholdValue = threshold.resolve(getAssociatedModelElement());
        boolean triggered = false;

        if (previousValue != null) {
            triggered = switch (direction) {
                case RISING_CROSS -> previousValue <= thresholdValue && currentValue > thresholdValue;
                case FALLING_CROSS -> previousValue >= thresholdValue && currentValue < thresholdValue;
            };
        }

        previousValue = currentValue;

        return triggered;
    }

    @Override
    public void run() {
        for (Action action : actions)
            action.apply(getAssociatedModelElement());
    }
}
