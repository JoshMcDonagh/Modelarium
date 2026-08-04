package modelarium.entities.logging.databases;

import modelarium.utils.Cloners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Clears all stored series and recorded value types.
     */
    @Override
    public void disconnect() {
        attributesMap.clear();
        attributeClassesMap.clear();
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
        attributesMap.computeIfAbsent(attributeName, k -> new ArrayList<>());
        if (attributeValue != null && !attributeClassesMap.containsKey(attributeName))
            attributeClassesMap.put(attributeName, attributeValue.getClass());

        if (attributeValue == null || attributeClassesMap.get(attributeName).isInstance(attributeValue)) {
            attributesMap.get(attributeName).add(Cloners.standard().deepClone(attributeValue));
        } else {
            Class<?> expectedType = attributeClassesMap.get(attributeName);
            throw new IllegalArgumentException("Attribute '" + attributeName + "' is not an instance of "
                    + expectedType.getSimpleName());
        }
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
        attributesMap.computeIfAbsent(attributeName, k -> new ArrayList<>());
        attributesMap.put(attributeName, attributeValues == null ? new ArrayList<>() : Cloners.standard().deepClone(attributeValues));

        Class<?> inferred = firstNonNullClass(attributeValues);
        if (inferred != null)
            attributeClassesMap.put(attributeName, inferred);
    }

    /**
     * Retrieves the named attribute's stored series.
     *
     * @param attributeName the name of the attribute whose series to retrieve
     * @return the attribute's stored values in insertion order, or null if none have been stored
     */
    @Override
    public List<Object> getAttributeColumnAsList(String attributeName) {
        return attributesMap.get(attributeName);
    }
}