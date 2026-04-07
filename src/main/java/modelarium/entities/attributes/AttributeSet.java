package modelarium.entities.attributes;

import modelarium.entities.contexts.Context;
import modelarium.entities.logging.AttributeSetLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeSet {
    private static int attributeSetCount = 0;

    private final String name;
    private final List<Attribute> attributeList;
    private final Map<String, Integer> attributeIndexMap = new HashMap<String, Integer>();
    private final AttributeSetLog log;

    public AttributeSet(String ownerName, String attributeSetName, List<Attribute> attributeList) {
        this.name = attributeSetName;
        this.attributeList = attributeList;
        for (int i = 0; i < this.attributeList.size(); i++) {
            Attribute attribute = this.attributeList.get(i);
            this.attributeIndexMap.put(attribute.name(), i);
        }
        this.log = new AttributeSetLog(ownerName, this.name, this.attributeList);
        attributeSetCount++;
    }

    public AttributeSet(String ownerName, List<Attribute> attributeList) {
        this(ownerName, "attribute_set_" + attributeSetCount, attributeList);
    }

    public String name() {
        return name;
    }

    public int size() {
        return attributeList.size();
    }

    public void setContext(Context context) {
        for (Attribute attribute : attributeList)
            attribute.setContext(context);
    }

    public Attribute get(int attributeIndex) throws IllegalAccessException {
        Attribute attribute = attributeList.get(attributeIndex);
        if (attribute.accessLevel() == AttributeAccessLevel.PUBLIC)
            return attribute;
        throw new IllegalAccessException(attribute.name() + " is a PRIVATE attribute and cannot be returned.");
    }

    public Attribute get(String attributeName) throws IllegalAccessException {
        Attribute attribute = attributeList.get(attributeIndexMap.get(attributeName));
        if (attribute.accessLevel() == AttributeAccessLevel.PUBLIC)
            return attribute;
        throw new IllegalAccessException(attribute.name() + " is a PRIVATE attribute and cannot be returned.");
    }

    public Event getEvent(int eventIndex) throws IllegalAccessException {
        Attribute attribute = get(eventIndex);

        if (attribute instanceof Event event)
            return event;

        if (attribute == null)
            return null;

        throw new IllegalArgumentException("Expected an Event, but got: " + attribute.getClass().getName());
    }

    public Event getEvent(String eventName) throws IllegalAccessException {
        return getEvent(attributeIndexMap.get(eventName));
    }

    public Routine getRoutine(int processIndex) throws IllegalAccessException {
        Attribute attribute = get(processIndex);

        if (attribute instanceof Routine routine)
            return routine;

        if (attribute == null)
            return null;

        throw new IllegalArgumentException("Expected a Routine, but got: " + attribute.getClass().getName());
    }

    public Routine getRoutine(String processName) throws IllegalAccessException {
        return getRoutine(attributeIndexMap.get(processName));
    }

    public Property<?> getProperty(int propertyIndex) throws IllegalAccessException {
        Attribute attribute = get(propertyIndex);

        if (attribute instanceof Property<?> property)
            return property;

        if (attribute == null)
            return null;

        throw new IllegalArgumentException("Expected a Property, but got: " + attribute.getClass().getName());
    }

    public Property<?> getProperty(String propertyName) throws IllegalAccessException {
        return getProperty(attributeIndexMap.get(propertyName));
    }

    public AttributeSetLog getLog() {
        return log;
    }

    public void run() {
        for (Attribute attribute : attributeList) {
            Object valueToLog = null;

            if (attribute instanceof Event) {
                Event event = (Event) attribute;
                boolean isTriggered = event.isTriggered();
                if (isTriggered)
                    event.run();
                valueToLog = isTriggered;

            } else if (attribute instanceof Property) {
                Property<?> property = (Property<?>) attribute;
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
