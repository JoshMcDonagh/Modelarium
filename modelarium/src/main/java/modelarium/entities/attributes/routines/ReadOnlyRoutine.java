package modelarium.entities.attributes.routines;

import modelarium.entities.attributes.ReadOnlyAttribute;

public final class ReadOnlyRoutine extends ReadOnlyAttribute<Routine<?>> {
    /**
     * Constructs a new immutable attribute wrapping the specified mutable attribute.
     *
     * @param attribute the mutable attribute to provide a read-only view of
     */
    ReadOnlyRoutine(Routine<?> attribute) {
        super(attribute);
    }
}
