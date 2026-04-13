package modelarium.results;

import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.results.immutable.ImmutableResultsForAgents;
import modelarium.results.immutable.ImmutableResultsForEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A concrete results container for the simulation environment.
 *
 * <p>Extends {@link ResultsForEntities} to store and access recorded property
 * and event values specific to the environment, using its name to simplify queries.
 */
public class ResultsForEnvironment extends ResultsForEntities<EnvironmentContext, EnvironmentAttributeSet, AttributeSetLog<EnvironmentContext>> {

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

    protected ResultsForEnvironment(ResultsForEnvironment other) {
        super(other);
        this.environmentName = other.environmentName;
    }

    public int attributeSetLogCount() {
        return entityAttributeSetLogCount(environmentName);
    }

    public int attributeSetLogCount(String attributeSetName) {
        return entityAttributeSetAttributeLogCount(environmentName, attributeSetName);
    }

    public List<Object> attributeLogs(String attributeSetName, String attributeName) {
        return getLogsForEntityAttribute(environmentName, attributeSetName, attributeName);
    }

    public <T> List<T> attributeLogs(String attributeSetName, String attributeName, Class<T> type) {
        List<Object> raw = getLogsForEntityAttribute(environmentName, attributeSetName, attributeName);
        List<T> typed = new ArrayList<>(raw.size());
        for (Object value : raw)
            typed.add(type.cast(value));
        return typed;
    }

    public Map<String, List<Object>> attributeSetLogs(String attributeSetName) {
        return getLogsForEntityAttributeSetAsMap(environmentName, attributeSetName);
    }

    public Map<String, Map<String, List<Object>>> environmentLogs() {
        return getLogsForEntityAsMap(environmentName);
    }

    public ImmutableResultsForEnvironment getAsImmutable() {
        return new ImmutableResultsForEnvironment(this);
    }
}
