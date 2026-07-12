package modelarium.entities.attributes;

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

public sealed abstract class AttributeSet<SC extends SimulationContext, C extends Context> permits AgentAttributeSet, EnvironmentAttributeSet {
    private final String ownerName;
    private final String name;
    private final List<AttributeBase<SC>> attributeList = new ArrayList<>();
    private final Map<String, Integer> attributeIndexMap = new HashMap<>();

    private AttributeSetLog<SC> log = null;

    private SC context = null;

    @SuppressWarnings("unchecked")
    AttributeSet(String ownerName, String attributeSetName, List<Attribute> attributeList) {
        this.ownerName = ownerName;
        this.name = attributeSetName;

        for (int i = 0; i < attributeList.size(); i++) {
            AttributeBase<SC> attribute = (AttributeBase<SC>) attributeList.get(i);
            this.attributeList.add(attribute);
            this.attributeIndexMap.put(attribute.name(), i);
        }
    }

    @Internal
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

    @Internal
    public void setContext(SC context) {
        if (this.context != null)
            return;

        for (AttributeBase<SC> attribute : attributeList)
            attribute.setContext(context);

        this.context = context;
    }

    private AttributeBase<C> getAttribute(int attributeIndex) {
        // noinspection unchecked
        AttributeBase<C> attribute = (AttributeBase<C>) attributeList.get(attributeIndex);
        if (attribute.accessLevel() == AttributeAccessLevel.PUBLIC)
            return attribute;
        throw new AttributeAccessException(attribute.name() + " is a PRIVATE attribute and cannot be returned.");
    }

    private AttributeBase<C> getAttribute(String attributeName) {
        // noinspection unchecked
        AttributeBase<C> attribute = (AttributeBase<C>) attributeList.get(attributeIndexMap.get(attributeName));
        if (attribute.accessLevel() == AttributeAccessLevel.PUBLIC)
            return attribute;
        throw new AttributeAccessException(attribute.name() + " is a PRIVATE attribute and cannot be returned.");
    }

    Attribute get(int index) {
        return (Attribute) getAttribute(index);
    }

    Attribute get(String attributeName) {
        return (Attribute) getAttribute(attributeIndexMap.get(attributeName));
    }

    Event<C> getEvent(int eventIndex) {
        AttributeBase<C> attribute = getAttribute(eventIndex);

        if (attribute instanceof Event<C> event)
            return event;

        throw new AttributeAccessException("Expected an Event, but got: " + attribute.getClass().getName());
    }

    Event<C> getEvent(String eventName) {
        return getEvent(attributeIndexMap.get(eventName));
    }

    Routine<C> getRoutine(int routineIndex) {
        AttributeBase<C> attribute = getAttribute(routineIndex);

        if (attribute instanceof Routine<C> routine)
            return routine;

        throw new AttributeAccessException("Expected a Routine, but got: " + attribute.getClass().getName());
    }

    Routine<C> getRoutine(String routineName) {
        return getRoutine(attributeIndexMap.get(routineName));
    }

    Property<?,C> getProperty(int propertyIndex) {
        AttributeBase<C> attribute = getAttribute(propertyIndex);

        if (attribute instanceof Property<?, C> property)
            return property;

        throw new AttributeAccessException("Expected a Property, but got: " + attribute.getClass().getName());
    }

    Property<?,C> getProperty(String propertyName) {
        return getProperty(attributeIndexMap.get(propertyName));
    }

    public AttributeSetLog<SC> getLog() {
        return log;
    }

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

            if (attribute.isLogged())
                log.record(attribute.name(), valueToLog);
        }
    }
}
