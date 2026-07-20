package modelarium.entities.logging.databases;

import java.util.List;

/**
 * Abstract base class representing a database for storing and retrieving simulation results
 * related to attribute sets, including properties and event values.
 *
 * <p>Concrete implementations may write to in-memory structures, files, or external systems.
 * This class supports both tick-by-tick updates and full-column writes.
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
     * <p>The default implementation does nothing; implementations backed by external resources should override this
     * to acquire them.
     */
    public void connect() {
        // Default implementation: No operation
        return;
    }

    /**
     * Closes the database and releases any held resources. Must be implemented by subclasses.
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
     * @return the attribute's stored values in insertion order
     */
    public abstract List<Object> getAttributeColumnAsList(String attributeName);
}
