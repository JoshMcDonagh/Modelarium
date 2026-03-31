package modelarium.attributes;

import modelarium.attributes.results.AttributeSetRunLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeSet {
    private static int attributeSetCount = 0;

    private final String name;
    private final List<Attribute> attributeList;
    private final Map<String, Integer> attributeIndexMap = new HashMap<String, Integer>();
    private final AttributeSetRunLog log;

    public AttributeSet(String ownerName, String attributeSetName, List<Attribute> attributeList) {
        this.name = attributeSetName;
        this.attributeList = attributeList;
        for (int i = 0; i < this.attributeList.size(); i++) {
            Attribute attribute = this.attributeList.get(i);
            this.attributeIndexMap.put(attribute.name(), i);
        }
        this.log = new AttributeSetRunLog(ownerName, this.name, this.attributeList);
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

    public Attribute get(int attributeIndex) {
        return attributeList.get(attributeIndex);
    }

    public Attribute get(String attributeName) {
        return get(attributeIndexMap.get(attributeName));
    }

    public Event getEvent(int eventIndex) {
        Attribute attribute = get(eventIndex);

        if (!(attribute instanceof Event event))
            throw new IllegalArgumentException("Expected an Event, but got: " + attribute.getClass().getName());

        return event;
    }

    public Event getEvent(String eventName) {
        return getEvent(attributeIndexMap.get(eventName));
    }

    public Routine getProcess(int processIndex) {
        Attribute attribute = get(processIndex);

        if (!(attribute instanceof Routine routine))
            throw new IllegalArgumentException("Expected a Process, but got: " + attribute.getClass().getName());

        return routine;
    }

    public Routine getProcess(String processName) {
        return getProcess(attributeIndexMap.get(processName));
    }

    public Property<?> getProperty(int propertyIndex) {
        Attribute attribute = get(propertyIndex);

        if (!(attribute instanceof Property<?> property))
            throw new IllegalArgumentException("Expected a Property, but got: " + attribute.getClass().getName());

        return property;
    }

    public Property<?> getProperty(String propertyName) {
        return getProperty(attributeIndexMap.get(propertyName));
    }

    public AttributeSetRunLog getLog() {
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
