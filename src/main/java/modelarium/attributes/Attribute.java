package modelarium.attributes;

import modelarium.ModelElement;
import utils.DeepCopyable;

import java.io.Serializable;

/**
 * Represents a named attribute associated with a model element such as an agent or environment.
 *
 * <p>Attributes define a unit of state or behaviour that is executed during a simulation tick
 * via the {@link #run()} method. Attributes may optionally be marked as "recorded" to indicate
 * that their output should be included in simulation results.
 *
 * <p>Each attribute may be associated with a specific {@link ModelElement} (e.g. an agent or
 * environment) to provide context during execution.
 */
public abstract class Attribute implements Serializable, DeepCopyable<Attribute> {

    /** Unique name identifying this attribute */
    private final String name;

    /** Whether this attribute should be recorded in simulation output */
    private final boolean isRecorded;

    /** The model element (e.g. agent or environment) this attribute is associated with */
    private ModelElement associatedModelElement;

    /**
     * Constructs an attribute with the given name and recording flag.
     *
     * @param name        the name of the attribute
     * @param isRecorded  whether the attribute should be included in recorded output
     */
    public Attribute(String name, boolean isRecorded) {
        this.name = name;
        this.isRecorded = isRecorded;
    }

    /**
     * Constructs a deep copy attribute from a given attribute
     *
     * @param other the attribute to deep copy
     */
    protected Attribute(Attribute other) {
        this.name = other.name;
        this.isRecorded = other.isRecorded;
        this.associatedModelElement = other.associatedModelElement;
    }

    /**
     * Associates this attribute with a specific model element.
     * Typically set internally by the simulation engine.
     *
     * @param associatedModelElement the model element to associate with
     */
    public void setAssociatedModelElement(ModelElement associatedModelElement) {
        this.associatedModelElement = associatedModelElement;
    }

    /**
     * Retrieves the model element this attribute is associated with.
     *
     * @return the associated {@link ModelElement}, or null if not set
     */
    protected ModelElement getAssociatedModelElement() {
        return associatedModelElement;
    }

    /**
     * @return the name of the attribute
     */
    public String getName() {
        return name;
    }

    /**
     * @return true if the attribute should be recorded in simulation results
     */
    public boolean isRecorded() {
        return isRecorded;
    }

    /**
     * Executes the logic associated with this attribute.
     * Called once per simulation tick.
     */
    public abstract void run();
}
