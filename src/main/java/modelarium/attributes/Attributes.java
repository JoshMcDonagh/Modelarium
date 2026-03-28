package modelarium.attributes;

import modelarium.ModelElement;
import utils.DeepCopyable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for managing a collection of {@link Attribute} instances.
 *
 * <p>This class provides the core internal structure for storing and looking up attributes
 * by name or index. It assumes attribute names are unique within the collection.
 *
 * <p>Subclasses are responsible for populating the collection and must define the behaviour
 * of the group during each simulation tick via the {@link #run()} method.
 */
public abstract class Attributes implements DeepCopyable<Attributes> {

    /** Maps attribute names to their index positions within the list */
    private final Map<String, Integer> attributeIndexes = new HashMap<>();

    /** Ordered list of attributes held by this collection */
    private final List<Attribute> attributes = new ArrayList<>();

    private ModelElement associatedModelElement;

    /**
     * Associates all contained attributes with the given model element.
     * Typically called once during initialisation.
     *
     * @param associatedModelElement the model element (e.g. agent or environment) to associate
     */
    public void setAssociatedModelElement(ModelElement associatedModelElement) {
        this.associatedModelElement = associatedModelElement;
        for (Attribute attribute : attributes)
            attribute.setAssociatedModelElement(associatedModelElement);
    }

    /**
     * Retrieves the model element associated with attributes.
     * @return the {@link ModelElement} instance
     */
    public ModelElement getAssociatedModelElement() {
        return associatedModelElement;
    }

    /**
     * Adds or replaces an attribute in the collection.
     * If an attribute with the same name already exists, it will be replaced
     * at its current index; otherwise, the new attribute is appended.
     *
     * @param attribute the attribute to add or update
     */
    protected void addAttribute(Attribute attribute) {
        int index;

        if (attributeIndexes.containsKey(attribute.getName())) {
            // Replace an existing attribute using its known index
            index = attributeIndexes.get(attribute.getName());
        } else {
            // Append a new attribute and register its index
            index = attributes.size();
            attributeIndexes.put(attribute.getName(), index);
            attributes.add(null); // Reserve space before inserting
        }

        attributes.set(index, attribute);
    }

    /**
     * Retrieves an attribute by its name.
     *
     * @param attributeName the name of the attribute to retrieve
     * @return the corresponding {@link Attribute} instance
     */
    protected Attribute getAttribute(String attributeName) {
        int index = attributeIndexes.get(attributeName);
        return attributes.get(index);
    }

    /**
     * Retrieves an attribute by its index in the list.
     *
     * @param index the index position of the attribute
     * @return the {@link Attribute} at the given index
     */
    protected Attribute getAttribute(int index) {
        return attributes.get(index);
    }

    /**
     * Checks whether an attribute of a given name exists.
     *
     * @param attributeName the name of the attribute to check for
     * @return  the boolean value reflecting whether the attribute exists or not
     */
    public boolean hasAttribute(String attributeName) {
        return attributeIndexes.containsKey(attributeName);
    }

    /**
     * @return the number of attributes held by this collection
     */
    public int size() {
        return attributes.size();
    }

    /**
     * Defines how the entire collection of attributes should be processed
     * during a simulation tick. Must be implemented by subclasses.
     */
    public abstract void run();
}
