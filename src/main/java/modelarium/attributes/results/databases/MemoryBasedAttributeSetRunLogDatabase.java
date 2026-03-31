package modelarium.attributes.results.databases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An in-memory implementation of {@link AttributeSetRunLogDatabase}.
 *
 * <p>This class stores all simulation results directly in RAM, using Java collections.
 * It is useful for lightweight simulations, unit tests, or post-processing before output.
 *
 * <p>Unlike {@link DiskBasedAttributeSetRunLogDatabase}, this class does not persist results to disk.
 */
public class MemoryBasedAttributeSetRunLogDatabase extends AttributeSetRunLogDatabase {

    // === Internal maps to store values by name ===
    private final Map<String, List<Object>> propertiesMap = new HashMap<>();
    private final Map<String, Class<?>> propertyClassesMap = new HashMap<>();

    private final Map<String, List<Object>> preEventsMap = new HashMap<>();
    private final Map<String, Class<?>> preEventClassesMap = new HashMap<>();

    private final Map<String, List<Object>> postEventsMap = new HashMap<>();
    private final Map<String, Class<?>> postEventClassesMap = new HashMap<>();

    private static Class<?> firstNonNullClass(List<?> values) {
        if (values == null) return null;
        for (Object v : values) {
            if (v != null) return v.getClass();
        }
        return null;
    }

    /**
     * Overrides setDatabasePath but ignores the value, as memory-based storage does not require a file path.
     *
     * @param databasePath ignored
     */
    @Override
    protected void setDatabasePath(String databasePath) {
        // No-op for in-memory implementation
    }

    // === Tick-by-tick value addition ===
    @Override
    public <T> void addPropertyValue(String propertyName, T propertyValue) {
        propertiesMap.computeIfAbsent(propertyName, k -> new ArrayList<>());
        // If we don't yet know the element type for this column, infer it from the first non-null value
        if (propertyValue != null && !propertyClassesMap.containsKey(propertyName)) {
            propertyClassesMap.put(propertyName, propertyValue.getClass());
        }
        // Allow nulls; otherwise enforce the recorded element type
        if (propertyValue == null || propertyClassesMap.get(propertyName).isInstance(propertyValue)) {
            propertiesMap.get(propertyName).add(propertyValue);
        } else {
            throw new IllegalArgumentException(
                    "Property '" + propertyName + "' is not an instance of " + propertyValue.getClass().getSimpleName());
        }
    }

    @Override
    public <T> void addPreEventValue(String preEventName, T preEventValue) {
        preEventsMap.computeIfAbsent(preEventName, k -> new ArrayList<>());
        if (preEventValue != null && !preEventClassesMap.containsKey(preEventName)) {
            preEventClassesMap.put(preEventName, preEventValue.getClass());
        }
        if (preEventValue == null || preEventClassesMap.get(preEventName).isInstance(preEventValue)) {
            preEventsMap.get(preEventName).add(preEventValue);
        } else {
            throw new IllegalArgumentException(
                    "Pre-event '" + preEventName + "' is not an instance of " + preEventValue.getClass().getSimpleName());
        }
    }

    @Override
    public <T> void addPostEventValue(String postEventName, T postEventValue) {
        postEventsMap.computeIfAbsent(postEventName, k -> new ArrayList<>());
        if (postEventValue != null && !postEventClassesMap.containsKey(postEventName)) {
            postEventClassesMap.put(postEventName, postEventValue.getClass());
        }
        if (postEventValue == null || postEventClassesMap.get(postEventName).isInstance(postEventValue)) {
            postEventsMap.get(postEventName).add(postEventValue);
        } else {
            throw new IllegalArgumentException(
                    "Post-event '" + postEventName + "' is not an instance of " + postEventValue.getClass().getSimpleName());
        }
    }

    // === Column replacement ===
    @Override
    public void setPropertyColumn(String propertyName, List<Object> propertyValues) {
        // Create the column if it doesn't exist, then replace the data
        propertiesMap.computeIfAbsent(propertyName, k -> new ArrayList<>());
        propertiesMap.put(propertyName, propertyValues == null ? new ArrayList<>() : new ArrayList<>(propertyValues));

        // Infer & record the element type from the first non-null value (if any)
        Class<?> inferred = firstNonNullClass(propertyValues);
        if (inferred != null) {
            propertyClassesMap.put(propertyName, inferred);
        }
    }

    @Override
    public void setPreEventColumn(String preEventName, List<Object> preEventValues) {
        preEventsMap.computeIfAbsent(preEventName, k -> new ArrayList<>());
        preEventsMap.put(preEventName, preEventValues == null ? new ArrayList<>() : new ArrayList<>(preEventValues));

        Class<?> inferred = firstNonNullClass(preEventValues);
        if (inferred != null) {
            preEventClassesMap.put(preEventName, inferred);
        }
    }

    @Override
    public void setPostEventColumn(String postEventName, List<Object> postEventValues) {
        postEventsMap.computeIfAbsent(postEventName, k -> new ArrayList<>());
        postEventsMap.put(postEventName, postEventValues == null ? new ArrayList<>() : new ArrayList<>(postEventValues));

        Class<?> inferred = firstNonNullClass(postEventValues);
        if (inferred != null) {
            postEventClassesMap.put(postEventName, inferred);
        }
    }

    // === Column retrieval ===
    @Override
    public List<Object> getPropertyColumnAsList(String propertyName) {
        return propertiesMap.get(propertyName);
    }

    @Override
    public List<Object> getPreEventColumnAsList(String preEventName) {
        return preEventsMap.get(preEventName);
    }

    @Override
    public List<Object> getPostEventColumnAsList(String postEventName) {
        return postEventsMap.get(postEventName);
    }
}
