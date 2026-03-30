package modelarium.environments;

import modelarium.ModelSettings;

/**
 * Default generator for creating the simulation environment.
 *
 * <p>This implementation simply duplicates the base attribute set collection
 * defined in the model settings, producing a standard environment instance.
 *
 * <p>The environment is named "Environment" by default and is intended for
 * general use when no custom generation logic is required.
 */
public class DefaultEnvironmentGenerator extends EnvironmentGenerator {

    /**
     * Generates the simulation environment using default settings.
     *
     * <p>This method creates a deep copy of the base environment attribute set
     * collection from the model settings and uses it to initialise a new
     * {@link Environment} instance named "Environment".
     *
     * @param modelSettings the model-wide settings containing the base attribute sets
     * @return a new environment with duplicated attributes
     */
    @Override
    public Environment generateEnvironment(ModelSettings modelSettings) {
        // Create a deep copy of the base environment attributes to ensure isolation
        AttributeSetCollection environmentAttributeSetCollection =
                modelSettings.getBaseEnvironmentAttributeSetCollection().deepCopy();

        // Return a standard environment instance named "Environment"
        return new Environment("Environment", environmentAttributeSetCollection);
    }
}
