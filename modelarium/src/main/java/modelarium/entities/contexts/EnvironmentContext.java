package modelarium.entities.contexts;

import modelarium.entities.attributes.sets.mutable.AttributeBase;
import modelarium.entities.attributes.sets.mutable.MutableEnvironmentAttributeSet;
import modelarium.entities.environments.Environment;

/**
 * Interface for providing the environment's attributes with access to their owning environment and the wider model.
 *
 * <p>This interface is the view of the simulation the environment's attributes are given, exposing the environment
 * itself along with the attribute set and attribute currently being run.
 */
public sealed interface EnvironmentContext extends EntityContext permits EnvironmentSimulationContext {

    /**
     * Returns the environment this context belongs to.
     *
     * @return the owning {@link Environment} instance
     */
    Environment getThisEntity();

    /**
     * Returns the attribute set currently being run on the owning environment.
     *
     * @return the current {@link MutableEnvironmentAttributeSet} instance
     */
    MutableEnvironmentAttributeSet getThisAttributeSet();

    /**
     * Returns the attribute currently being run on the owning environment.
     *
     * @return the current attribute instance
     */
    AttributeBase<EnvironmentSimulationContext> getThisAttribute();
}
