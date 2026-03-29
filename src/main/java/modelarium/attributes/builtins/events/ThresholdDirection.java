package modelarium.attributes.builtins.events;

/**
 * Defines the direction of threshold crossing that a
 * {@link modelarium.attributes.builtins.events.ThresholdCrossingEvent} should detect.
 *
 * <p>{@link #RISING_CROSS} triggers when a value moves from at or below the threshold to above it.
 * {@link #FALLING_CROSS} triggers when a value moves from at or above the threshold to below it.</p>
 */
public enum ThresholdDirection {
    /**
     * A crossing from at or below the threshold to above the threshold.
     */
    RISING_CROSS,

    /**
     * A crossing from at or above the threshold to below the threshold.
     */
    FALLING_CROSS
}
