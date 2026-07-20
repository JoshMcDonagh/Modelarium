package modelarium.entities.logging;

import modelarium.entities.attributes.AttributeBase;
import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.attributes.properties.Property;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.logging.databases.AttributeSetLogDatabase;
import modelarium.entities.logging.databases.factories.AttributeSetLogDatabaseFactory;
import modelarium.utils.Cloners;

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
public class AttributeSetLog<C extends SimulationContext> {

    /** Name of the agent or environment this result set belongs to */
    private final String ownerName;

    /** Name of the attribute set being logged */
    private final String attributeSetName;

    /** The database instance used to store this attribute set’s results */
    private final AttributeSetLogDatabase database;

    /** Names of all logged attributes */
    private final List<String> attributeNamesList = new ArrayList<>();

    /** Maps property names to their runtime class types */
    private final Map<String, Class<?>> propertyTypesMap = new HashMap<>();

    /**
     * Constructs a new attribute set log, creating and connecting its backing database and registering the
     * attributes marked for logging.
     *
     * @param ownerName the name of the agent or environment the logged attribute set belongs to
     * @param attributeSetName the name of the attribute set being logged
     * @param databaseFactory the factory used to create the log's backing database
     * @param attributeList the attribute set's attributes, from which the logged ones are registered
     */
    public AttributeSetLog(
            String ownerName,
            String attributeSetName,
            AttributeSetLogDatabaseFactory databaseFactory,
            List<AttributeBase<C>> attributeList
    ) {
        this.ownerName = ownerName;
        this.attributeSetName = attributeSetName;
        this.database = databaseFactory.create();

        database.connect();

        // Register attributes marked for logging
        for (AttributeBase<C> attribute : attributeList) {
            if (!attribute.isLogged())
                continue;

            attributeNamesList.add(attribute.name());

            if (attribute instanceof Property<?,C>)
                propertyTypesMap.put(attribute.name(), ((Property<?,C>) attribute).type());
        }
    }

    /**
     * Returns the name of the model element this log belongs to.
     *
     * @return the name of the owning model element (agent/environment)
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Returns the name of the attribute set being logged.
     *
     * @return the name of the attribute set being logged
     */
    public String getAttributeSetName() {
        return attributeSetName;
    }

    /**
     * Returns the names of the attributes registered for logging.
     *
     * @return a new list of names of logged attributes
     */
    public List<String> getAttributeNamesList() {
        return new ArrayList<>(attributeNamesList);
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

    /**
     * Returns the number of attributes registered for logging.
     *
     * @return the log's attribute count
     */
    public int attributeLogCount() {
        return attributeNamesList.size();
    }

    /**
     * Records a value for the named attribute at the current tick.
     *
     * @param attributeName the name of the attribute the value belongs to
     * @param value the value to record
     */
    public void record(String attributeName, Object value) {
        database.addAttributeValue(attributeName, value);
    }

    /**
     * Retrieves the values recorded for the named attribute across the model run.
     *
     * @param attributeName the name of the attribute whose values to retrieve
     * @return a deep clone of the attribute's recorded values, one per tick
     */
    public List<Object> getValues(String attributeName) {
        return Cloners.standard().deepClone(database.getAttributeColumnAsList(attributeName));
    }

    /**
     * Closes the underlying database and releases any held resources.
     */
    public void disconnectDatabase() {
        database.disconnect();
    }
}
