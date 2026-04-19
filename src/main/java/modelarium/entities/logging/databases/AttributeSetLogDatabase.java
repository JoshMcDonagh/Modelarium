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
    private final String databasePath;

    public AttributeSetLogDatabase(String databasePath) {
        this.databasePath = databasePath;
    }

    public AttributeSetLogDatabase() {
        this.databasePath = null;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void connect() {
        // Default implementation: No operation
        return;
    }

    public abstract void disconnect();

    public abstract <T> void addAttributeValue(String attributeName, T attributeValue);

    public abstract void setAttributeColumn(String attributeName, List<Object> propertyValues);

    public abstract List<Object> getAttributeColumnAsList(String attributeName);
}
