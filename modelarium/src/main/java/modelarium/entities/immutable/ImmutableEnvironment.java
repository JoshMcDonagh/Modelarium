package modelarium.entities.immutable;

import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.immutable.attributes.ImmutableEnvironmentAttributeSet;
import modelarium.entities.logging.AttributeSetLog;

/**
 * Class for providing a read-only view of an {@link Environment}.
 *
 * <p>This class wraps the mutable environment so that other model elements can inspect it without being able to
 * modify it.
 */
public final class ImmutableEnvironment extends ImmutableEntity<EnvironmentSimulationContext, EnvironmentContext, EnvironmentAttributeSet, AttributeSetLog<EnvironmentSimulationContext>> {

    /**
     * Constructs a new immutable environment wrapping the specified mutable environment.
     *
     * @param entity the mutable environment to provide a read-only view of
     */
    public ImmutableEnvironment(Environment entity) {
        super(entity);
    }

    /**
     * Retrieves a read-only view of one of the wrapped environment's attribute sets by index.
     *
     * @param attributeSetIndex the index of the attribute set to retrieve
     * @return a new {@link ImmutableEnvironmentAttributeSet} instance wrapping the attribute set at the specified
     *         index
     */
    @Override
    public ImmutableEnvironmentAttributeSet getAttributeSet(int attributeSetIndex) {
        return new ImmutableEnvironmentAttributeSet(getMutableEntity().getAttributeSet(attributeSetIndex));
    }

    /**
     * Retrieves a read-only view of one of the wrapped environment's attribute sets by name.
     *
     * @param attributeSetName the name of the attribute set to retrieve
     * @return a new {@link ImmutableEnvironmentAttributeSet} instance wrapping the attribute set with the specified
     *         name
     */
    @Override
    public ImmutableEnvironmentAttributeSet getAttributeSet(String attributeSetName) {
        return new ImmutableEnvironmentAttributeSet(getMutableEntity().getAttributeSet(attributeSetName));
    }
}
