package modelarium.results.mutable;

import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.results.ResultsForEnvironment;
import modelarium.results.immutable.ImmutableResultsForEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A concrete results container for the simulation environment.
 *
 * <p>Extends {@link MutableResultsForEntities} to store and access recorded property
 * and event values specific to the environment, using its name to simplify queries.
 */
public final class MutableResultsForEnvironment extends MutableResultsForEntities<EnvironmentSimulationContext, EnvironmentContext, EnvironmentAttributeSet, AttributeSetLog<EnvironmentSimulationContext>> implements ResultsForEnvironment {

    /** The name of the environment used as a key for data access */
    private final String environmentName;

    /**
     * Constructs a results container for the given environment.
     *
     * @param environment the environment whose results are to be stored
     */
    public MutableResultsForEnvironment(Environment environment) {
        super(environment);
        this.environmentName = environment.name();
    }

    @Override
    public int attributeSetLogCount() {
        return entityAttributeSetLogCount(environmentName);
    }

    @Override
    public int attributeLogCount(String attributeSetName) {
        return entityAttributeSetAttributeLogCount(environmentName, attributeSetName);
    }

    @Override
    public List<Object> attributeLogs(String attributeSetName, String attributeName) {
        return getLogsForEntityAttribute(environmentName, attributeSetName, attributeName);
    }

    @Override
    public <T> List<T> attributeLogs(String attributeSetName, String attributeName, Class<T> type) {
        List<Object> raw = getLogsForEntityAttribute(environmentName, attributeSetName, attributeName);
        List<T> typed = new ArrayList<>(raw.size());
        for (Object value : raw)
            typed.add(type.cast(value));
        return typed;
    }

    @Override
    public Map<String, List<Object>> attributeSetLogs(String attributeSetName) {
        return getLogsForEntityAttributeSetAsMap(environmentName, attributeSetName);
    }

    @Override
    public Map<String, Map<String, List<Object>>> environmentLogs() {
        return getLogsForEntityAsMap(environmentName);
    }

    public ImmutableResultsForEnvironment getAsImmutable() {
        return new ImmutableResultsForEnvironment(this);
    }
}
