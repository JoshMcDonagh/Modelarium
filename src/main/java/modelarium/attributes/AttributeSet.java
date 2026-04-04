package modelarium.attributes;

import modelarium.contexts.Context;
import modelarium.logging.AttributeSetLog;
import modelarium.logging.databases.factories.AttributeSetLogDatabaseFactory;

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

    public Attribute get(int attributeIndex) {
        Attribute attribute = attributeList.get(attributeIndex);
        if (attribute.accessLevel() == AttributeAccessLevel.PUBLIC)
            return attribute;
        return null;
    }

    public Attribute get(String attributeName) {
        Attribute attribute = attributeList.get(attributeIndexMap.get(attributeName));
        if (attribute.accessLevel() == AttributeAccessLevel.PUBLIC)
            return attribute;
        return null;
    }

    public Event getEvent(int eventIndex) {
        Attribute attribute = get(eventIndex);

        if (attribute instanceof Event event)
            return event;

        if (attribute == null)
            return null;

        throw new IllegalArgumentException("Expected an Event, but got: " + attribute.getClass().getName());
    }

    public Event getEvent(String eventName) {
        return getEvent(attributeIndexMap.get(eventName));
    }

    public Routine getRoutine(int processIndex) {
        Attribute attribute = get(processIndex);

        if (attribute instanceof Routine routine)
            return routine;

        if (attribute == null)
            return null;

        throw new IllegalArgumentException("Expected a Routine, but got: " + attribute.getClass().getName());
    }

    public Routine getRoutine(String processName) {
        return getRoutine(attributeIndexMap.get(processName));
    }

    public Property<?> getProperty(int propertyIndex) {
        Attribute attribute = get(propertyIndex);

        if (attribute instanceof Property<?> property)
            return property;

        if (attribute == null)
            return null;

        throw new IllegalArgumentException("Expected a Property, but got: " + attribute.getClass().getName());
    }

    public Property<?> getProperty(String propertyName) {
        return getProperty(attributeIndexMap.get(propertyName));
    }

    public AttributeSetLog getLog() {
        return log;
    }

    public void run() {
        for (Attribute attribute : attributeList) {

            if (attribute instanceof Event) {
                Event event = (Event) attribute;
                boolean isTriggered = event.isTriggered();
                if (isTriggered)
                    event.run();

            } else if (attribute instanceof Property) {
                Property<?> property = (Property<?>) attribute;
                property.run();

            } else {
                attribute.run();
            }

            log.record(attribute);
        }
    }
}
