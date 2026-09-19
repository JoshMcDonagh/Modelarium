package modelarium.entities.logging.databases;

import java.util.List;

/**
 * Abstract base class representing a database for storing and retrieving simulation results
 * related to attribute sets, including properties and event values.
 *
 * <p>Concrete implementations may write to in-memory structures, files, or external systems. Every implementation
 * follows the same observable contract:
 * <ul>
 *     <li>{@link #connect()} must be called before reading or writing;</li>
 *     <li>connecting and disconnecting are idempotent;</li>
 *     <li>disconnecting discards the stored data, so reconnecting starts with an empty store;</li>
 *     <li>a missing or cleared series is returned as an empty list;</li>
 *     <li>null values are preserved but do not establish a series type;</li>
 *     <li>the first non-null appended value establishes the series type;</li>
 *     <li>all subsequent appended values must be instances of that type;</li>
 *     <li>replacing a series establishes a new type from its first non-null value and requires every other non-null
 *     value in the replacement to have that type; and</li>
 *     <li>inputs are copied on write and results are copied on read.</li>
 * </ul>
 */
public abstract class AttributeSetLogDatabase {

    /** The path of the database's backing file, or null for databases without one */
    private final String databasePath;

    /**
     * Constructs a new attribute set log database backed by a file at the specified path.
     *
     * @param databasePath the path of the database's backing file
     */
    public AttributeSetLogDatabase(String databasePath) {
        this.databasePath = databasePath;
    }

    /**
     * Constructs a new attribute set log database with no backing file.
     */
    public AttributeSetLogDatabase() {
        this.databasePath = null;
    }

    /**
     * Returns the path of the database's backing file.
     *
     * @return the database's file path, or null if the database has no backing file
     */
    public String getDatabasePath() {
        return databasePath;
    }

    /**
     * Opens the database ready for reading and writing.
     *
     * <p>Repeated calls while connected must have no effect and must preserve stored values.
     */
    public abstract void connect();

    /**
     * Closes the database, discards all stored data and releases any held resources.
     *
     * <p>Repeated calls while disconnected must have no effect.
     */
    public abstract void disconnect();

    /**
     * Appends a value to the named attribute's stored series. Must be implemented by subclasses.
     *
     * @param attributeName the name of the attribute the value belongs to
     * @param attributeValue the value to append
     * @param <T> the type of the value being appended
     */
    public abstract <T> void addAttributeValue(String attributeName, T attributeValue);

    /**
     * Replaces the named attribute's stored series with the given values. Must be implemented by subclasses.
     *
     * @param attributeName the name of the attribute the values belong to
     * @param propertyValues the values to store as the attribute's series
     */
    public abstract void setAttributeColumn(String attributeName, List<Object> propertyValues);

    /**
     * Retrieves the named attribute's stored series. Must be implemented by subclasses.
     *
     * @param attributeName the name of the attribute whose series to retrieve
     * @return a detached list containing the attribute's stored values in insertion order, or an empty list when the
     * series does not exist
     */
    public abstract List<Object> getAttributeColumnAsList(String attributeName);
}
