package modelarium.attributes.builtins.actions;

import modelarium.Entity;

/**
 * Represents a built-in operation that can be applied to a {@link Entity}.
 *
 * <p>Built-in events use {@code Action} implementations to modify properties on the model element
 * when the event is triggered. Actions are intentionally lightweight and typically operate by
 * locating a target property and then mutating it in some simple way.</p>
 *
 * <p>This interface is intended for use by the built-in event system, but custom implementations
 * may also be provided where a reusable mutation is useful.</p>
 */
public interface Action {
    /**
     * Applies this action to the supplied model element.
     *
     * @param element the model element to mutate
     */
    void apply(Entity element);
}
