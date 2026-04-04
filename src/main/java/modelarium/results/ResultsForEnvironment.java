package modelarium.results;

import modelarium.entities.environments.Environment;

import java.util.List;
import java.util.Map;

/**
 * A concrete results container for the simulation environment.
 *
 * <p>Extends {@link ResultsForEntities} to store and access recorded property
 * and event values specific to the environment, using its name to simplify queries.
 */
public class ResultsForEnvironment extends ResultsForEntities {

    /** The name of the environment used as a key for data access */
    private final String environmentName;

    /**
     * Constructs a results container for the given environment.
     *
     * @param environment the environment whose results are to be stored
     */
    public ResultsForEnvironment(Environment environment) {
        super(environment);
        this.environmentName = environment.name();
    }

    public int attributeSetLogCount() {
        return entityAttributeSetLogCount(environmentName);
    }

    public int attributeSetLogCount(String attributeSetName) {
        return entityAttributeSetAttributeLogCount(environmentName, attributeSetName);
    }

    public List<Object> attributeLogs(String attributeSetName, String attributeName) {
        return getLogsForEntityAttribute(this.environmentName, attributeSetName, attributeName);
    }

    public Map<String, List<Object>> attributeSetLogs(String attributeSetName) {
        return getLogsForEntityAttributeSetAsMap(environmentName, attributeSetName);
    }

    public Map<String, Map<String, List<Object>>> environmentLogs() {
        return getLogsForEntityAsMap(environmentName);
    }
}
