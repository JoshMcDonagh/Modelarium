package modelarium.entities.logging.databases;

import modelarium.utils.Cloners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An in-memory implementation of {@link AttributeSetLogDatabase}.
 *
 * <p>This class stores all simulation results directly in RAM, using Java collections.
 * It is useful for lightweight simulations, unit tests, or post-processing before output.
 *
 * <p>Unlike {@link DiskBasedAttributeSetLogDatabase}, this class does not persist results to disk.
 */
public class MemoryBasedAttributeSetLogDatabase extends AttributeSetLogDatabase {

    /** Maps each attribute's name to its stored series of values */
    private final Map<String, List<Object>> attributesMap = new HashMap<>();

    /** Maps each attribute's name to the class of the values it stores */
    private final Map<String, Class<?>> attributeClassesMap = new HashMap<>();

    /** Whether this database is currently open for reading and writing */
    private boolean connected = false;

    /**
     * Returns the class of the first non-null value in a list.
     *
     * @param values the values to inspect
     * @return the class of the first non-null value, or null if there is none
     */
    private static Class<?> firstNonNullClass(List<?> values) {
        if (values == null) return null;
        for (Object v : values) {
            if (v != null) return v.getClass();
        }
        return null;
    }

    /**
     * Constructs a new in-memory attribute set log database.
     */
    public MemoryBasedAttributeSetLogDatabase() {
        super();
    }

    /** Opens this database for reading and writing. */
    @Override
    public void connect() {
        connected = true;
    }

    /**
     * Clears all stored series and recorded value types.
     */
    @Override
    public void disconnect() {
        attributesMap.clear();
        attributeClassesMap.clear();
        connected = false;
    }

    /**
     * Appends a value to the named attribute's stored series, checking that it matches the type of the values
     * already stored.
     *
     * @param attributeName the name of the attribute the value belongs to
     * @param attributeValue the value to append
     * @param <T> the type of the value being appended
     */
    @Override
    public <T> void addAttributeValue(String attributeName, T attributeValue) {
        ensureConnected();
        Objects.requireNonNull(attributeName, "attributeName must not be null");
        attributesMap.computeIfAbsent(attributeName, k -> new ArrayList<>());
        Class<?> expectedType = attributeClassesMap.get(attributeName);
        validateValueType(attributeName, attributeValue, expectedType);

        attributesMap.get(attributeName).add(Cloners.standard().deepClone(attributeValue));
        if (attributeValue != null && expectedType == null)
            attributeClassesMap.put(attributeName, attributeValue.getClass());
    }

    /**
     * Replaces the named attribute's stored series with a copy of the given values, inferring the attribute's value
     * type from the first non-null value.
     *
     * @param attributeName the name of the attribute the values belong to
     * @param attributeValues the values to store as the attribute's series
     */
    @Override
    public void setAttributeColumn(String attributeName, List<Object> attributeValues) {
        ensureConnected();
        Objects.requireNonNull(attributeName, "attributeName must not be null");

        Class<?> inferred = firstNonNullClass(attributeValues);
        validateColumnTypes(attributeName, attributeValues, inferred);

        attributesMap.put(
                attributeName,
                attributeValues == null ? new ArrayList<>() : deepCloneValues(attributeValues)
        );

        attributeClassesMap.remove(attributeName);
        if (inferred != null)
            attributeClassesMap.put(attributeName, inferred);
    }

    /**
     * Retrieves the named attribute's stored series.
     *
     * @param attributeName the name of the attribute whose series to retrieve
     * @return a detached list containing the attribute's stored values in insertion order, or an empty list when the
     * series does not exist
     */
    @Override
    public List<Object> getAttributeColumnAsList(String attributeName) {
        ensureConnected();
        Objects.requireNonNull(attributeName, "attributeName must not be null");
        return deepCloneValues(attributesMap.getOrDefault(attributeName, List.of()));
    }

    private static List<Object> deepCloneValues(List<?> values) {
        List<Object> clonedValues = new ArrayList<>(values.size());
        for (Object value : values)
            clonedValues.add(Cloners.standard().deepClone(value));
        return clonedValues;
    }

    private static void validateColumnTypes(String attributeName, List<?> values, Class<?> expectedType) {
        if (values == null || expectedType == null)
            return;

        for (Object value : values)
            validateValueType(attributeName, value, expectedType);
    }

    private static void validateValueType(String attributeName, Object value, Class<?> expectedType) {
        if (value != null && expectedType != null && !expectedType.isInstance(value))
            throw new IllegalArgumentException("Attribute '" + attributeName + "' requires values of type "
                    + expectedType.getName() + " but received " + value.getClass().getName());
    }

    private void ensureConnected() {
        if (!connected)
            throw new IllegalStateException("Database connection has not been established. Call connect() first.");
    }
}
