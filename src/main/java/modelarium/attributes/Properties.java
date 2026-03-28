package modelarium.attributes;

import java.util.List;

/**
 * A concrete collection class for managing {@link Property} attributes.
 *
 * <p>Properties are typed, stateful attributes that expose `get()` and `set()` methods
 * and are evaluated each tick via their {@link Property#run()} method.
 *
 * <p>This class extends {@link Attributes} and stores only {@code Property<?>} instances.
 */
public class Properties extends Attributes {

    /**
     * Adds a single property to the collection.
     *
     * @param property the {@link Property} to add
     * @param <T> the type of the property's value
     */
    public <T> void add(Property<T> property) {
        addAttribute(property);
    }

    /**
     * Adds a list of properties to the collection.
     *
     * @param properties a list of {@link Property} instances
     */
    public void add(List<Property<?>> properties) {
        for (Property<?> property : properties)
            add(property);
    }

    /**
     * Retrieves a property by name.
     *
     * @param name the name of the property
     * @return the matching {@link Property}, or null if not found
     */
    public Property<?> get(String name) {
        return (Property<?>) getAttribute(name);
    }

    /**
     * Retrieves a property by index.
     *
     * @param index the position of the property
     * @return the {@link Property} at the specified index
     */
    public Property<?> get(int index) {
        return (Property<?>) getAttribute(index);
    }

    /**
     * Executes all properties' {@code run()} methods in sequence.
     * This is typically called once per simulation tick.
     */
    @Override
    public void run() {
        for (int i = 0; i < size(); i++)
            get(i).run();
    }

    @Override
    public Properties deepCopy() {
        Properties propertiesCopy = new Properties();
        for (int i = 0; i < size(); i++)
            propertiesCopy.add(get(i).deepCopy());
        return propertiesCopy;
    }

    /**
     * Checks if a property of a given name exists.
     *
     * @param name  the name of the property to check for
     * @return  the boolean value reflecting whether the property exists or not
     */
    public boolean contains(String name) {
        return hasAttribute(name);
    }
}
