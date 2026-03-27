package modelarium.attributes.builtins.events;

import modelarium.attributes.Event;
import modelarium.attributes.builtins.actions.Action;
import modelarium.attributes.builtins.refs.DoubleValueRef;
import modelarium.attributes.builtins.util.BuiltinLookup;

import java.util.List;

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
