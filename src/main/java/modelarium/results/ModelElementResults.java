package modelarium.results;

import modelarium.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container class for storing and accessing results from one or more {@link Entity}s,
 * including agents and the environment.
 *
 * <p>Each model element has an associated {@link AttributeSetCollectionResults} instance,
 * which records its attribute outputs over time.
 *
 * <p>This class provides methods to query and merge results, as well as to disconnect
 * any underlying database resources.
 */
public class ModelElementResults {

    /** Maps model element names to their attribute set collection results */
    private final Map<String, AttributeSetCollectionResults> attributeSetCollectionResultsMap = new HashMap<>();

    /** Ordered list of all attribute set collection results (used for iteration or indexing) */
    private final List<AttributeSetCollectionResults> attributeSetCollectionResultsList = new ArrayList<>();

    /**
     * Constructs a new results container from a list of model elements.
     *
     * @param modelElements the model elements (agents or environment)
     */
    public ModelElementResults(List<? extends Entity> modelElements) {
        for (Entity entity : modelElements)
            addAttributeSetCollectionResults(entity.getAttributeSetCollection().getResults());
    }

    /**
     * Constructs a new results container for a single model element.
     *
     * @param entity the agent or environment to track results for
     */
    public ModelElementResults(Entity entity) {
        addAttributeSetCollectionResults(entity.getAttributeSetCollection().getResults());
    }

    /**
     * Adds a set of attribute collection results to the internal maps/lists.
     *
     * @param results the results to add
     */
    private void addAttributeSetCollectionResults(AttributeSetCollectionResults results) {
        attributeSetCollectionResultsMap.put(results.getModelElementName(), results);
        attributeSetCollectionResultsList.add(results);
    }

    /**
     * Merges another results object into this one.
     *
     * @param otherResults the results to merge
     */
    public void mergeWith(ModelElementResults otherResults) {
        attributeSetCollectionResultsMap.putAll(otherResults.attributeSetCollectionResultsMap);
        attributeSetCollectionResultsList.addAll(otherResults.attributeSetCollectionResultsList);
    }

    /**
     * Retrieves the recorded values for a property from a specific attribute set and model element.
     *
     * @param modelElementName the name of the agent/environment
     * @param attributeSetName the name of the attribute set
     * @param propertyName the name of the property
     * @return a list of recorded property values
     */
    public List<Object> getPropertyValues(String modelElementName, String attributeSetName, String propertyName) {
        return attributeSetCollectionResultsMap.get(modelElementName)
                .getAttributeSetResults(attributeSetName)
                .getPropertyValues(propertyName);
    }

    /**
     * Retrieves the recorded trigger values for a pre-event.
     *
     * @param modelElementName the name of the agent/environment
     * @param attributeSetName the attribute set's name
     * @param eventName the event's name
     * @return a list of boolean values representing event triggers
     */
    public List<Boolean> getPreEventValues(String modelElementName, String attributeSetName, String eventName) {
        return attributeSetCollectionResultsMap.get(modelElementName)
                .getAttributeSetResults(attributeSetName)
                .getPreEventValues(eventName);
    }

    /**
     * Retrieves the recorded trigger values for a post-event.
     *
     * @param modelElementName the name of the agent/environment
     * @param attributeSetName the attribute set's name
     * @param eventName the event's name
     * @return a list of boolean values representing event triggers
     */
    public List<Boolean> getPostEventValues(String modelElementName, String attributeSetName, String eventName) {
        return attributeSetCollectionResultsMap.get(modelElementName)
                .getAttributeSetResults(attributeSetName)
                .getPostEventValues(eventName);
    }

    /**
     * Retrieves the {@link AttributeSetCollectionResults} for a specific model element.
     *
     * @param modelElementName the model element's name
     * @return the associated attribute results
     */
    public AttributeSetCollectionResults getAttributeSetCollectionResults(String modelElementName) {
        return attributeSetCollectionResultsMap.get(modelElementName);
    }

    /**
     * Retrieves an {@link AttributeSetCollectionResults} by its index.
     *
     * @param index the index of the result
     * @return the results at the given index
     */
    public AttributeSetCollectionResults getAttributeSetCollectionResults(int index) {
        return attributeSetCollectionResultsList.get(index);
    }

    /**
     * @return the number of attribute set collections stored
     */
    public int getAttributeSetCollectionSetCount() {
        return attributeSetCollectionResultsList.size();
    }

    /**
     * Disconnects all underlying databases associated with stored attribute set results.
     * Should be called when results are no longer needed to free resources.
     */
    public void disconnectDatabases() {
        for (AttributeSetCollectionResults results : attributeSetCollectionResultsList)
            results.disconnectDatabases();

        attributeSetCollectionResultsList.clear();
        attributeSetCollectionResultsMap.clear();
    }
}
