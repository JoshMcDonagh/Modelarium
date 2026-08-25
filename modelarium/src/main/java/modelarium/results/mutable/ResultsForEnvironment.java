package modelarium.results.mutable;

import modelarium.entities.attributes.sets.mutable.EnvironmentAttributeSet;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.EntityLog;
import modelarium.results.immutable.ReadOnlyResultsForEnvironment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A concrete results container for the simulation environment.
 *
 * <p>Extends {@link ResultsForEntities} to store and access recorded property
 * and event values specific to the environment, using its name to simplify queries.
 */
public final class ResultsForEnvironment extends ResultsForEntities<EnvironmentSimulationContext, EnvironmentContext, EnvironmentAttributeSet, AttributeSetLog<EnvironmentSimulationContext>> {

    /** The name of the environment used as a key for data access */
    private final String environmentName;

    /** The immutable version of this mutable environment-level results */
    private ReadOnlyResultsForEnvironment immutableResultsForEnvironment = null;

    /**
     * Constructs a results container for the given environment.
     *
     * @param environment the environment whose results are to be stored
     */
    public ResultsForEnvironment(Environment environment) {
        super(environment);
        this.environmentName = environment.name();
    }

    /**
     * Returns the number of attribute set logs recorded for the environment.
     *
     * @return the environment's attribute set log count
     */
    public int attributeSetLogCount() {
        return entityAttributeSetLogCount(environmentName);
    }

    /**
     * Returns the number of attribute logs recorded in the environment's named attribute set.
     *
     * @param attributeSetName the name of the attribute set whose logs to count
     * @return the attribute set's attribute log count
     */
    public int attributeLogCount(String attributeSetName) {
        return entityAttributeSetAttributeLogCount(environmentName, attributeSetName);
    }

    /**
     * Retrieves the values logged for a single attribute of the environment.
     *
     * @param attributeSetName the name of the attribute set the attribute belongs to
     * @param attributeName the name of the attribute whose values to retrieve
     * @return the attribute's logged values, one per tick
     */
    public List<Object> attributeLogs(String attributeSetName, String attributeName) {
        return getLogsForEntityAttribute(environmentName, attributeSetName, attributeName);
    }

    /**
     * Retrieves the values logged for a single attribute of the environment, cast to the given type.
     *
     * @param attributeSetName the name of the attribute set the attribute belongs to
     * @param attributeName the name of the attribute whose values to retrieve
     * @param type the class to cast each logged value to
     * @param <T> the type the logged values are returned as
     * @return the attribute's logged values, one per tick
     */
    public <T> List<T> attributeLogs(String attributeSetName, String attributeName, Class<T> type) {
        List<Object> raw = getLogsForEntityAttribute(environmentName, attributeSetName, attributeName);
        List<T> typed = new ArrayList<>(raw.size());
        for (Object value : raw)
            typed.add(type.cast(value));
        return typed;
    }

    /**
     * Retrieves the values logged for every attribute in one of the environment's attribute sets.
     *
     * @param attributeSetName the name of the attribute set whose logs to retrieve
     * @return a map from attribute name to that attribute's logged values
     */
    public Map<String, List<Object>> attributeSetLogs(String attributeSetName) {
        return getLogsForEntityAttributeSetAsMap(environmentName, attributeSetName);
    }

    /**
     * Retrieves the values logged for every attribute of the environment.
     *
     * @return a map from attribute set name to a map from attribute name to that attribute's logged values
     */
    public Map<String, Map<String, List<Object>>> environmentLogs() {
        return getLogsForEntityAsMap(environmentName);
    }

    /**
     * Returns a read-only view of these environment results.
     *
     * @return a new {@link ReadOnlyResultsForEnvironment} instance wrapping these results
     */
    public ReadOnlyResultsForEnvironment getAsImmutable() {
        if (immutableResultsForEnvironment == null)
            immutableResultsForEnvironment = new ReadOnlyResultsForEnvironment(this);

        return immutableResultsForEnvironment;
    }

    /**
     * Exports the results of the environment to the given export path
     *
     * @param exportPath the path results are to be exported to
     */
    @Override
    void export(Path exportPath) {
        EntityLog<
                EnvironmentSimulationContext,
                EnvironmentContext,
                EnvironmentAttributeSet,
                AttributeSetLog<EnvironmentSimulationContext>
                > environmentLog = getEntityLogList().getFirst();

        Path environmentResultsExportPath = exportPath.resolve("environment");

        try {
            Files.createDirectories(environmentResultsExportPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create path: " + environmentResultsExportPath, e);
        }

        exportEntityResults(environmentLog, environmentResultsExportPath);
    }
}
