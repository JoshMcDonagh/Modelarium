package modelarium.attributes.results;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds the result containers for all {@link AttributeSet} instances
 * associated with a single model element (e.g. an agent or the environment).
 *
 * <p>This class provides access to individual {@link AttributeSetResults},
 * indexed both by name and position. It is created during model setup and
 * reused during each tick for recording attribute values.
 */
public class AttributeSetCollectionResults {

    /** The name of the model element (e.g., agent/environment) this result collection belongs to */
    private String modelElementName;

    /** Ordered list of result objects (preserving attribute set order) */
    private final List<AttributeSetResults> attributeSetResultsList = new ArrayList<>();

    /** Fast lookup of result objects by attribute set name */
    private final Map<String, AttributeSetResults> attributeSetResultsMap = new HashMap<>();

    /**
     * Prepares this result container for a given model element and its attribute sets.
     *
     * @param modelElementName the name of the owning agent or environment
     * @param attributeSets the list of attribute sets to track results for
     */
    public void setup(String modelElementName, List<AttributeSet> attributeSets) {
        this.modelElementName = modelElementName;

        for (AttributeSet attributeSet : attributeSets) {
            AttributeSetResults attributeSetResults = new AttributeSetResults(this.modelElementName, attributeSet);
            attributeSetResultsList.add(attributeSetResults);
            attributeSetResultsMap.put(attributeSet.getName(), attributeSetResults);
        }
    }

    /**
     * Retrieves the results object for a given attribute set name.
     *
     * @param attributeSetName the name of the attribute set
     * @return the associated {@link AttributeSetResults}
     */
    public AttributeSetResults getAttributeSetResults(String attributeSetName) {
        return attributeSetResultsMap.get(attributeSetName);
    }

    /**
     * Retrieves the results object by index.
     *
     * @param index the index of the attribute set
     * @return the corresponding {@link AttributeSetResults}
     */
    public AttributeSetResults getAttributeSetResults(int index) {
        return attributeSetResultsList.get(index);
    }

    /**
     * @return the number of attribute sets tracked in this collection
     */
    public int getAttributeSetCount() {
        return attributeSetResultsList.size();
    }

    /**
     * @return the name of the model element this result set belongs to
     */
    public String getModelElementName() {
        return modelElementName;
    }

    /**
     * Closes and clears all result databases associated with this collection.
     *
     * <p>This should be called when the simulation ends or when the data is no longer needed.
     */
    public void disconnectDatabases() {
        for (AttributeSetResults attributeSetResults : attributeSetResultsList)
            attributeSetResults.disconnectDatabase();

        attributeSetResultsMap.clear();
        attributeSetResultsList.clear();
    }
}
