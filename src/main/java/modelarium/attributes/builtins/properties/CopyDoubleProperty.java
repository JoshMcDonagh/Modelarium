package modelarium.attributes.builtins.properties;

import modelarium.attributes.Property;

/**
 * A derived {@link modelarium.attributes.Property} that exposes the current value of another
 * {@code Double} property on the same associated model element.
 *
 * <p>The source property is identified by attribute set name and property name. This property does
 * not maintain its own independent stored value. Instead, {@link #get()} performs a lookup and
 * returns the source property's current value each time it is called.</p>
 *
 * <p>Because this property is derived from another property, {@link #set(Double)} is not supported
 * and {@link #run()} is a no-op.</p>
 *
 * <p>This class is useful for aliasing or exposing a property under another attribute set structure,
 * or for building simple derived views without duplicating state.</p>
 *
 * <p>The source attribute set and source property must exist, and the source property must be a
 * {@code Double} property accessible from the associated model element.</p>
 */
public class CopyDoubleProperty extends Property<Double>  {
    private final String sourceAttributeSetName;
    private final String sourceAttributeName;

    public CopyDoubleProperty(String name, boolean isRecorded, String sourceAttributeSetName, String sourceAttributeName) {
        super(name, isRecorded, Double.class);
        this.sourceAttributeSetName = sourceAttributeSetName;
        this.sourceAttributeName = sourceAttributeName;
    }

    public CopyDoubleProperty(boolean isRecorded, String sourceAttributeSetName, String sourceAttributeName) {
        super(isRecorded, Double.class);
        this.sourceAttributeSetName = sourceAttributeSetName;
        this.sourceAttributeName = sourceAttributeName;
    }

    public CopyDoubleProperty(String name, String sourceAttributeSetName, String sourceAttributeName) {
        super(name, Double.class);
        this.sourceAttributeSetName = sourceAttributeSetName;
        this.sourceAttributeName = sourceAttributeName;
    }

    public CopyDoubleProperty(String sourceAttributeSetName, String sourceAttributeName) {
        super(Double.class);
        this.sourceAttributeSetName = sourceAttributeSetName;
        this.sourceAttributeName = sourceAttributeName;
    }

    public CopyDoubleProperty(CopyDoubleProperty other) {
        super(other);
        this.sourceAttributeSetName = other.sourceAttributeSetName;
        this.sourceAttributeName = other.sourceAttributeName;
    }

    @Override
    public void set(Double value) {
        throw new UnsupportedOperationException("CopyDoubleProperty is derived and cannot be set");
    }

    @Override
    public Double get() {
        var element = getAssociatedModelElement();
        if (element == null)
            throw new IllegalStateException("CopyDoubleProperty has no associated ModelElement");

        var sourceSet = element.getAttributeSetCollection().get(sourceAttributeSetName);
        if (sourceSet == null)
            throw new IllegalStateException("Source attribute set not found: " + sourceAttributeSetName);

        var properties = sourceSet.getProperties();
        if (!properties.contains(sourceAttributeName))
            throw new IllegalStateException("Source property not found: " + sourceAttributeName + " in " + sourceAttributeSetName);

        var source = properties.get(sourceAttributeName);
        return (Double) source.get();
    }

    @Override
    public void run() {
        // no-op
    }
}
