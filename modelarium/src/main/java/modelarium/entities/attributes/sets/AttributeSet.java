package modelarium.entities.attributes.sets;

import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.AttributeBase;
import modelarium.entities.attributes.events.Event;
import modelarium.entities.attributes.properties.Property;
import modelarium.entities.attributes.routines.Routine;
import modelarium.entities.contexts.Context;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.databases.factories.AttributeSetLogDatabaseFactory;
import modelarium.exceptions.AttributeAccessException;
import modelarium.internal.Internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for containing and managing a named group of an entity's attributes.
 *
 * <p>This class is responsible for storing an entity's attributes, providing typed and access-checked retrieval of
 * events, routines and properties, running each attribute in order every tick, and recording the values of logged
 * attributes. It is extended by {@link AgentAttributeSet} and {@link EnvironmentAttributeSet}.
 *
 * @param <SC> the type of simulation context this attribute set uses
 * @param <C> the type of context interface this attribute set's attributes are given
 */
public sealed abstract class AttributeSet<SC extends SimulationContext, C extends Context> permits AgentAttributeSet, EnvironmentAttributeSet {

    /** The name of the entity that owns this attribute set */
    private String ownerName = null;

    /** The name of this attribute set, used to identify it within its owning entity */
    private final String name;

    /** The attributes this set contains, in the order they are run */
    private final List<AttributeBase<SC>> attributeList = new ArrayList<>();

    /** Maps each attribute's name to its index in the attribute list */
    private final Map<String, Integer> attributeIndexMap = new HashMap<>();

    /** The indices of the attributes in the attribute list that are events */
    private final List<Integer> eventIndexList = new ArrayList<>();

    /** The indices of the attributes in the attribute list that are properties */
    private final List<Integer> propertyIndexList = new ArrayList<>();

    /** The indices of the attributes in the attribute list that are routines */
    private final List<Integer> routineIndexList = new ArrayList<>();

    /** The log recording the values of this set's logged attributes, created once by the model */
    private AttributeSetLog<SC> log = null;

    /** The simulation context this set's attributes use, set once by the owning entity */
    private SC context = null;

    /**
     * Constructs a new attribute set with the specified owner, name and attributes.
     *
     * @param name the name of the attribute set, used to identify it within its owning entity
     * @param attributeList the attributes the set will contain, in the order they will be run
     */
    @SuppressWarnings("unchecked")
    protected AttributeSet(String name, List<Attribute> attributeList) {
        this.name = name;

        for (int i = 0; i < attributeList.size(); i++) {
            AttributeBase<SC> attribute = (AttributeBase<SC>) attributeList.get(i);
            this.attributeList.add(attribute);
            this.attributeIndexMap.put(attribute.name(), i);

            if (attribute instanceof Event)
                this.eventIndexList.add(i);
            else if (attribute instanceof Property)
                this.propertyIndexList.add(i);
            else if (attribute instanceof Routine)
                this.routineIndexList.add(i);
            else
                throw new IllegalArgumentException("'" + attribute.name() + "' is not a valid attribute type");
        }
    }

    @Internal
    public void setOwnerName(String ownerName) {
        if (ownerName == null)
            return;
        this.ownerName = ownerName;
    }

    /**
     * Provides this attribute set with the factory used to create its log database, creating the set's log if it
     * does not already exist.
     *
     * @param database the factory the set will use to create its log database
     */
    @Internal
    public void setLogDatabaseFactory(AttributeSetLogDatabaseFactory database) {
        if (log != null)
            return;

        log = new AttributeSetLog<>(ownerName, name, database, attributeList);
    }

    /**
     * Returns the name of this attribute set.
     *
     * @return the attribute set's name
     */
    public String name() {
        return name;
    }

    /**
     * Returns the number of attributes in this set.
     *
     * @return the attribute set's size
     */
    public int size() {
        return attributeList.size();
    }

    /**
     * Provides this attribute set and each of its attributes with the simulation context they will use, if a context
     * has not already been set.
     *
     * @param context the context to provide the set and its attributes with
     */
    @Internal
    public void setContext(SC context) {
        if (this.context != null)
            return;

        for (AttributeBase<SC> attribute : attributeList)
            attribute.setContext(context);

        this.context = context;
    }

    /**
     * Retrieves an attribute by index, checking that it is publicly accessible.
     *
     * @param attributeIndex the index of the attribute to retrieve
     * @return the attribute at the specified index
     */
    private AttributeBase<C> getAttribute(int attributeIndex) {
        // noinspection unchecked
        AttributeBase<C> attribute = (AttributeBase<C>) attributeList.get(attributeIndex);
        if (attribute.accessLevel() == AttributeAccessLevel.PUBLIC)
            return attribute;
        throw new AttributeAccessException(attribute.name() + " is a PRIVATE attribute and cannot be returned.");
    }

    /**
     * Retrieves an attribute by name, checking that it is publicly accessible.
     *
     * @param attributeName the name of the attribute to retrieve
     * @return the attribute with the specified name
     */
    private AttributeBase<C> getAttribute(String attributeName) {
        // noinspection unchecked
        AttributeBase<C> attribute = (AttributeBase<C>) attributeList.get(attributeIndexMap.get(attributeName));
        if (attribute.accessLevel() == AttributeAccessLevel.PUBLIC)
            return attribute;
        throw new AttributeAccessException(attribute.name() + " is a PRIVATE attribute and cannot be returned.");
    }

    /**
     * Retrieves a publicly accessible attribute by index.
     *
     * @param index the index of the attribute to retrieve
     * @return the attribute at the specified index
     */
    public Attribute get(int index) {
        return getAttribute(index);
    }

    /**
     * Retrieves a publicly accessible attribute by name.
     *
     * @param attributeName the name of the attribute to retrieve
     * @return the attribute with the specified name
     */
    public Attribute get(String attributeName) {
        return getAttribute(attributeName);
    }

    /**
     * Retrieves an attribute by index, checking that it is an event.
     *
     * @param eventIndex the index of the attribute in the attribute list
     * @return the event at the specified index
     */
    private Event<C> getEventAttribute(int eventIndex) {
        AttributeBase<C> attribute = getAttribute(eventIndex);

        if (attribute instanceof Event<C> event)
            return event;

        throw new AttributeAccessException("Expected an Event, but got: " + attribute.getClass().getName());
    }

    /**
     * Retrieves an event by its index among this set's events.
     *
     * @param eventIndex the index of the event to retrieve
     * @return the event at the specified index
     */
    public Event<C> getEvent(int eventIndex) {
        return getEventAttribute(eventIndexList.get(eventIndex));
    }

    /**
     * Retrieves an event by name.
     *
     * @param eventName the name of the event to retrieve
     * @return the event with the specified name
     */
    public Event<C> getEvent(String eventName) {
        return getEventAttribute(attributeIndexMap.get(eventName));
    }

    /**
     * Retrieves an attribute by index, checking that it is a routine.
     *
     * @param routineIndex the index of the attribute in the attribute list
     * @return the routine at the specified index
     */
    private Routine<C> getRoutineAttribute(int routineIndex) {
        AttributeBase<C> attribute = getAttribute(routineIndex);

        if (attribute instanceof Routine<C> routine)
            return routine;

        throw new AttributeAccessException("Expected an Routine, but got: " + attribute.getClass().getName());
    }

    /**
     * Retrieves a routine by its index among this set's routines.
     *
     * @param routineIndex the index of the routine to retrieve
     * @return the routine at the specified index
     */
    public Routine<C> getRoutine(int routineIndex) {
        return getRoutineAttribute(routineIndexList.get(routineIndex));
    }

    /**
     * Retrieves a routine by name.
     *
     * @param routineName the name of the routine to retrieve
     * @return the routine with the specified name
     */
    public Routine<C> getRoutine(String routineName) {
        return getRoutineAttribute(attributeIndexMap.get(routineName));
    }

    /**
     * Retrieves an attribute by index, checking that it is a property.
     *
     * @param propertyIndex the index of the attribute in the attribute list
     * @return the property at the specified index
     */
    private Property<?,C> getPropertyAttribute(int propertyIndex) {
        AttributeBase<C> attribute = getAttribute(propertyIndex);

        if (attribute instanceof Property<?, C> property)
            return property;

        throw new AttributeAccessException("Expected a Property, but got: " + attribute.getClass().getName());
    }

    /**
     * Retrieves a property by its index among this set's properties.
     *
     * @param propertyIndex the index of the property to retrieve
     * @return the property at the specified index
     */
    public Property<?,C> getProperty(int propertyIndex) {
        return getPropertyAttribute(propertyIndexList.get(propertyIndex));
    }

    /**
     * Retrieves a property by name.
     *
     * @param propertyName the name of the property to retrieve
     * @return the property with the specified name
     */
    public Property<?,C> getProperty(String propertyName) {
        return getPropertyAttribute(attributeIndexMap.get(propertyName));
    }

    /**
     * Returns the log recording the values of this set's logged attributes.
     *
     * @return the attribute set's {@link AttributeSetLog} instance
     */
    public AttributeSetLog<SC> getLog() {
        return log;
    }

    /**
     * Runs each of this set's attributes in order for the current tick, recording the values of logged attributes.
     *
     * <p>Events are only run if triggered, with the trigger state logged; properties are run and their value logged;
     * routines are simply run.
     */
    public void run() {
        context.setCurrentAttributeSet(this);
        for (AttributeBase<SC> attribute : attributeList) {
            context.setCurrentAttribute(attribute);
            Object valueToLog = null;

            if (attribute instanceof Event<SC> event) {
                boolean isTriggered = event.isTriggered();
                if (isTriggered)
                    event.run();
                valueToLog = isTriggered;

            } else if (attribute instanceof Property<?, SC> property) {
                property.run();
                valueToLog = property.get();

            } else {
                attribute.run();
            }

            if (attribute.isLogged()) {
                if (log == null) {
                    throw new IllegalStateException(
                            "Log has not been initialised for attribute set '"
                                    + name
                                    + "' owned by '"
                                    + ownerName
                                    + "'"
                    );
                }

                log.record(attribute.name(), valueToLog);
            }
        }
    }
}
