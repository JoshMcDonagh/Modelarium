package modelarium.logging.databases;

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

    private final Map<String, List<Object>> attributesMap = new HashMap<>();
    private final Map<String, Class<?>> attributeClassesMap = new HashMap<>();

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

    @Override
    public <T> void addAttributeValue(String attributeName, T attributeValue) {
        attributesMap.computeIfAbsent(attributeName, k -> new ArrayList<>());
        if (attributeValue != null && !attributeClassesMap.containsKey(attributeName)) {
            attributeClassesMap.put(attributeName, attributeValue.getClass());
        }
        if (attributeValue == null || attributeClassesMap.get(attributeName).isInstance(attributeValue)) {
            attributesMap.get(attributeName).add(attributeValue);
        } else {
            throw new IllegalArgumentException(
                    "Attribute '" + attributeName + "' is not an instance of " + attributeValue.getClass().getSimpleName());
        }
    }

    @Override
    public void setAttributeColumn(String attributeName, List<Object> attributeValues) {
        attributesMap.computeIfAbsent(attributeName, k -> new ArrayList<>());
        attributesMap.put(attributeName, attributeValues == null ? new ArrayList<>() : new ArrayList<>(attributeValues));

        Class<?> inferred = firstNonNullClass(attributeValues);
        if (inferred != null) {
            attributeClassesMap.put(attributeName, inferred);
        }
    }

    @Override
    public List<Object> getAttributeColumnAsList(String attributeName) {
        return attributesMap.get(attributeName);
    }
}