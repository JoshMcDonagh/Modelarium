package modelarium.results;

import modelarium.environments.Environment;

import java.util.List;
import java.util.Map;

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

    public List<Object> getLogForEnvironmentAttribute(String attributeSetName, String attributeName) {
        return getLogForEntityAttribute(this.environmentName, attributeSetName, attributeName);
    }

    public int EnvironmentAttributeSetLogCount() {
        return entityAttributeSetLogCount(environmentName);
    }

    public int EnvironmentAttributeSetAttributeLogCount(String attributeSetName) {
        return entityAttributeSetAttributeLogCount(environmentName, attributeSetName);
    }

    public Map<String, List<Object>> getLogsForEnvironmentAttributeSetAsMap(String attributeSetName) {
        return getLogsForEntityAttributeSetAsMap(environmentName, attributeSetName);
    }

    public Map<String, Map<String, List<Object>>> getLogsForEnvironmentAsMap() {
        return getLogsForEntityAsMap(environmentName);
    }
}
