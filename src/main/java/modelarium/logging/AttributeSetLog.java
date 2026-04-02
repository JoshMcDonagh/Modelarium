package modelarium.logging;

import modelarium.attributes.Attribute;
import modelarium.attributes.AttributeSet;
import modelarium.attributes.Event;
import modelarium.attributes.Property;
import modelarium.logging.databases.AttributeSetRunLogDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores and manages the logged results for a single {@link AttributeSet},
 * including properties and events marked for logging.
 *
 * <p>This class is responsible for writing tick-level data to the backing database,
 * and for providing access to stored values after simulation.
 */
public class AttributeSetLog {

    /** Name of the agent or environment this result set belongs to */
    private final String ownerName;

    /** Name of the attribute set being logged */
    private final String attributeSetName;

    /** The database instance used to store this attribute set’s results */
    private final AttributeSetRunLogDatabase database;

    /** Names of all logged attributes */
    private final List<String> attributeNamesList = new ArrayList<>();

    /** Maps property names to their runtime class types */
    private final Map<String, Class<?>> propertyTypesMap = new HashMap<>();

    public AttributeSetLog(String ownerName, String attributeSetName, List<Attribute> attributeList) {
        this.ownerName = ownerName;
        this.attributeSetName = attributeSetName;
        this.database = AttributeSetRunLogDatabaseFactory.createDatabase();

        assert database != null;
        database.connect();

        // Register attributes marked for logging
        for (Attribute attribute : attributeList) {
            if (!attribute.isLogged())
                continue;

            attributeNamesList.add(attribute.name());

            if (attribute instanceof Property<?>)
                propertyTypesMap.put(attribute.name(), ((Property<?>) attribute).type());
        }
    }

    /** @return the name of the owning model element (agent/environment) */
    public String getOwnerName() {
        return ownerName;
    }

    /** @return the name of the attribute set being logged */
    public String getAttributeSetName() {
        return attributeSetName;
    }

    /** @return list of names of logged attributes */
    public List<String> getAttributeNamesList() {
        return attributeNamesList;
    }

    /**
     * Returns the recorded value type of a given property.
     *
     * @param propertyName the name of the property
     * @return the class type of the property's values
     */
    public Class<?> getPropertyType(String propertyName) {
        return propertyTypesMap.get(propertyName);
    }

    public void record(Attribute attribute) {
        if (!attribute.isLogged())
            return;

        String attributeName = attribute.name();
        Object value = null;

        if (attribute instanceof Event)
            value = ((Event)attribute).isTriggered();

        else if (attribute instanceof Property)
            value = ((Property<?>)attribute).get();

        database.addAttributeValue(attributeName, value);
    }

    public List<Object> getValues(String attributeName) {
        return database.getAttributeColumnAsList(attributeName);
    }

    /**
     * Closes the underlying database and releases any held resources.
     */
    public void disconnectDatabase() {
        database.disconnect();
    }
}
