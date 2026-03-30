package modelarium.attributes.results;

import modelarium.attributes.results.databases.AttributeSetResultsDatabase;
import modelarium.attributes.results.databases.AttributeSetResultsDatabaseFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores and manages the recorded results for a single {@link AttributeSet},
 * including properties and events marked for recording.
 *
 * <p>This class is responsible for writing tick-level data to the backing database,
 * and for providing access to stored values after simulation.
 */
public class AttributeSetResults {

    /** Name of the agent or environment this result set belongs to */
    private final String modelElementName;

    /** Name of the attribute set being recorded */
    private final String attributeSetName;

    /** The database instance used to store this attribute set’s results */
    private final AttributeSetResultsDatabase database;

    /** Names of all recorded properties in this attribute set */
    private final List<String> propertyNamesList = new ArrayList<>();

    /** Maps property names to their runtime class types */
    private final Map<String, Class<?>> propertyTypesMap = new HashMap<>();

    /** Names of all recorded pre-events */
    private final List<String> preEventNamesList = new ArrayList<>();

    /** Names of all recorded post-events */
    private final List<String> postEventNamesList = new ArrayList<>();

    /**
     * Constructs the results container for a given attribute set.
     *
     * <p>This initialises a backing database and indexes the recorded attributes
     * by name for fast access and later analysis.
     *
     * @param modelElementName the name of the model element (agent or environment)
     * @param attributeSet the attribute set this result container tracks
     */
    public AttributeSetResults(String modelElementName, AttributeSet attributeSet) {
        this.modelElementName = modelElementName;
        this.attributeSetName = attributeSet.getName();
        this.database = AttributeSetResultsDatabaseFactory.createDatabase();

        assert database != null;
        database.connect();

        // Register properties marked for recording
        for (int i = 0; i < attributeSet.getProperties().size(); i++) {
            Property<?> property = attributeSet.getProperties().get(i);
            if (property.isRecorded()) {
                propertyNamesList.add(property.getName());
                propertyTypesMap.put(property.getName(), property.getType());
            }
        }

        // Register pre-events marked for recording
        for (int i = 0; i < attributeSet.getPreEvents().size(); i++) {
            Event event = attributeSet.getPreEvents().get(i);
            if (event.isRecorded())
                preEventNamesList.add(event.getName());
        }

        // Register post-events marked for recording
        for (int i = 0; i < attributeSet.getPostEvents().size(); i++) {
            Event event = attributeSet.getPostEvents().get(i);
            if (event.isRecorded())
                postEventNamesList.add(event.getName());
        }
    }

    /** @return the name of the owning model element (agent/environment) */
    public String getModelElementName() {
        return modelElementName;
    }

    /** @return the name of the attribute set being recorded */
    public String getAttributeSetName() {
        return attributeSetName;
    }

    /** @return list of names of recorded properties */
    public List<String> getPropertyNamesList() {
        return propertyNamesList;
    }

    /** @return list of names of recorded pre-events */
    public List<String> getPreEventNamesList() {
        return preEventNamesList;
    }

    /** @return list of names of recorded post-events */
    public List<String> getPostEventNamesList() {
        return postEventNamesList;
    }

    /**
     * Returns the recorded value type of a given property.
     *
     * @param propertyName the name of the property
     * @return the class type of the property's values
     */
    public Class<?> getPropertyClass(String propertyName) {
        return propertyTypesMap.get(propertyName);
    }

    /**
     * Records a property value to the backing database for this tick.
     *
     * @param propertyName the property to record
     * @param value the value to store
     */
    public void recordProperty(String propertyName, Object value) {
        database.addPropertyValue(propertyName, value);
    }

    /**
     * Records a pre-event trigger status for this tick.
     *
     * @param eventName the pre-event name
     * @param isTriggered whether it was triggered
     */
    public void recordPreEvent(String eventName, boolean isTriggered) {
        database.addPreEventValue(eventName, isTriggered);
    }

    /**
     * Records a post-event trigger status for this tick.
     *
     * @param eventName the post-event name
     * @param isTriggered whether it was triggered
     */
    public void recordPostEvent(String eventName, boolean isTriggered) {
        database.addPostEventValue(eventName, isTriggered);
    }

    /**
     * Retrieves all recorded values for a property.
     *
     * @param propertyName the property name
     * @return list of recorded values
     */
    public List<Object> getPropertyValues(String propertyName) {
        return database.getPropertyColumnAsList(propertyName);
    }

    /**
     * Retrieves all recorded trigger states for a pre-event.
     *
     * @param eventName the pre-event name
     * @return list of trigger states per tick
     */
    @SuppressWarnings("unchecked")
    public List<Boolean> getPreEventValues(String eventName) {
        return (List<Boolean>) (List<?>) database.getPreEventColumnAsList(eventName);
    }

    /**
     * Retrieves all recorded trigger states for a post-event.
     *
     * @param eventName the post-event name
     * @return list of trigger states per tick
     */
    @SuppressWarnings("unchecked")
    public List<Boolean> getPostEventValues(String eventName) {
        return (List<Boolean>) (List<?>) database.getPostEventColumnAsList(eventName);
    }

    /**
     * Closes the underlying database and releases any held resources.
     */
    public void disconnectDatabase() {
        database.disconnect();
    }
}
