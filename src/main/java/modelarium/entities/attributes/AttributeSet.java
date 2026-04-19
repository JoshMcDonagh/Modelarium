package modelarium.entities.attributes;

import modelarium.entities.attributes.events.Event;
import modelarium.entities.attributes.properties.Property;
import modelarium.entities.attributes.routines.Routine;
import modelarium.entities.contexts.Context;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.databases.factories.AttributeSetLogDatabaseFactory;
import modelarium.exceptions.AttributeAccessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeSet<C extends Context> {
    private final String ownerName;
    private final String name;
    private final List<Attribute<C>> attributeList;
    private final Map<String, Integer> attributeIndexMap = new HashMap<>();

    private AttributeSetLog<C> log = null;

    private C context = null;

    AttributeSet(String ownerName, String attributeSetName, List<Attribute<C>> attributeList) {
        this.ownerName = ownerName;
        this.name = attributeSetName;
        this.attributeList = attributeList;
        for (int i = 0; i < this.attributeList.size(); i++) {
            Attribute<?> attribute = this.attributeList.get(i);
            this.attributeIndexMap.put(attribute.name(), i);
        }
    }

    public void setLogDatabaseFactory(AttributeSetLogDatabaseFactory database) {
        if (log != null)
            return;

        log = new AttributeSetLog<>(ownerName, name, database, attributeList);
    }

    public String name() {
        return name;
    }

    public int size() {
        return attributeList.size();
    }

    public void setContext(C context) {
        if (this.context != null)
            return;

        for (Attribute<C> attribute : attributeList)
            attribute.setContext(context);

        this.context = context;
    }

    public C context() {
        return context;
    }

    private Attribute<C> get(int attributeIndex) {
        Attribute<C> attribute = attributeList.get(attributeIndex);
        if (attribute.accessLevel() == AttributeAccessLevel.PUBLIC)
            return attribute;
        throw new AttributeAccessException(attribute.name() + " is a PRIVATE attribute and cannot be returned.");
    }

    private Attribute<C> get(String attributeName) {
        Attribute<C> attribute = attributeList.get(attributeIndexMap.get(attributeName));
        if (attribute.accessLevel() == AttributeAccessLevel.PUBLIC)
            return attribute;
        throw new AttributeAccessException(attribute.name() + " is a PRIVATE attribute and cannot be returned.");
    }

    Event<C> getEvent(int eventIndex) {
        Attribute<C> attribute = get(eventIndex);

        if (attribute instanceof Event<C> event)
            return event;

        if (attribute == null)
            return null;

        throw new AttributeAccessException("Expected an Event, but got: " + attribute.getClass().getName());
    }

    Event<C> getEvent(String eventName) {
        return getEvent(attributeIndexMap.get(eventName));
    }

    Routine<C> getRoutine(int processIndex) {
        Attribute<C> attribute = get(processIndex);

        if (attribute instanceof Routine<C> routine)
            return routine;

        if (attribute == null)
            return null;

        throw new AttributeAccessException("Expected a Routine, but got: " + attribute.getClass().getName());
    }

    Routine<C> getRoutine(String processName) {
        return getRoutine(attributeIndexMap.get(processName));
    }

    Property<?,C> getProperty(int propertyIndex) {
        Attribute<C> attribute = get(propertyIndex);

        if (attribute instanceof Property<?,C> property)
            return property;

        if (attribute == null)
            return null;

        throw new AttributeAccessException("Expected a Property, but got: " + attribute.getClass().getName());
    }

    Property<?,C> getProperty(String propertyName) {
        return getProperty(attributeIndexMap.get(propertyName));
    }

    public AttributeSetLog<C> getLog() {
        return log;
    }

    public void run() {
        context.setCurrentAttributeSet(this);
        for (Attribute<C> attribute : attributeList) {
            context.setCurrentAttribute(attribute);
            Object valueToLog = null;

            if (attribute instanceof Event<C> event) {
                boolean isTriggered = event.isTriggered();
                if (isTriggered)
                    event.run();
                valueToLog = isTriggered;

            } else if (attribute instanceof Property<?, C> property) {
                property.run();
                valueToLog = property.get();

            } else {
                attribute.run();
            }

            if (attribute.isLogged())
                log.record(attribute.name(), valueToLog);
        }
    }
}
