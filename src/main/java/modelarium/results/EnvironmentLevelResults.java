package modelarium.results;

import modelarium.environments.Environment;

import java.util.List;

/**
 * A concrete results container for the simulation environment.
 *
 * <p>Extends {@link EntityLevelResults} to store and access recorded property
 * and event values specific to the environment, using its name to simplify queries.
 */
public class EnvironmentLevelResults extends EntityLevelResults {

    /** The name of the environment used as a key for data access */
    private final String environmentName;

    /**
     * Constructs a results container for the given environment.
     *
     * @param environment the environment whose results are to be stored
     */
    public EnvironmentLevelResults(Environment environment) {
        super(environment);
        this.environmentName = environment.name();
    }

    /**
     * Retrieves all recorded values of a given property from the specified attribute set.
     *
     * @param attributeSetName the name of the attribute set
     * @param propertyName the name of the property
     * @return a list of recorded property values
     */
    public List<Object> getPropertyValues(String attributeSetName, String propertyName) {
        return getPropertyValues(environmentName, attributeSetName, propertyName);
    }

    /**
     * Retrieves all recorded trigger states of a pre-event from the specified attribute set.
     *
     * @param attributeSetName the name of the attribute set
     * @param eventName the name of the event
     * @return a list of booleans indicating whether the event was triggered
     */
    public List<Boolean> getPreEventValues(String attributeSetName, String eventName) {
        return getPreEventValues(environmentName, attributeSetName, eventName);
    }

    /**
     * Retrieves all recorded trigger states of a post-event from the specified attribute set.
     *
     * @param attributeSetName the name of the attribute set
     * @param eventName the name of the event
     * @return a list of booleans indicating whether the event was triggered
     */
    public List<Boolean> getPostEventValues(String attributeSetName, String eventName) {
        return getPostEventValues(environmentName, attributeSetName, eventName);
    }

    /**
     * Returns the full results object for the environment's attribute sets.
     *
     * @return the environment's attribute set collection results
     */
    public AttributeSetCollectionResults getAttributeSetCollectionResults() {
        return getAttributeSetCollectionResults(environmentName);
    }
}
