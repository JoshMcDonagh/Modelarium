package modelarium.logging.databases;

import com.rits.cloning.Cloner;
import utils.RandomStringGenerator;

import java.util.List;

/**
 * Abstract base class representing a database for storing and retrieving simulation results
 * related to attribute sets, including properties and event values.
 *
 * <p>Concrete implementations may write to in-memory structures, files, or external systems.
 * This class supports both tick-by-tick updates and full-column writes.
 */
public abstract class AttributeSetRunLogDatabase {
    private static final Cloner cloner = new Cloner();

    public static AttributeSetRunLogDatabase setupAndClone(AttributeSetRunLogDatabase database) {
        database.setDatabasePath(RandomStringGenerator.generateUniqueRandomString(20) + ".db");
        return cloner.deepClone(database);
    }

    private String databasePath = null;

    protected void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void connect() {
        // Default implementation: No operation
        return;
    }

    public void disconnect() {
        // Default implementation: No operation
        return;
    }

    public abstract <T> void addAttributeValue(String attributeName, T attributeValue);

    public abstract void setAttributeColumn(String attributeName, List<Object> propertyValues);

    public abstract List<Object> getAttributeColumnAsList(String attributeName);
}
