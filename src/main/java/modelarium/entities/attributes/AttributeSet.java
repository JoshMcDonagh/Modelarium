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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public sealed abstract class AttributeSet<SC extends SimulationContext, C extends Context> permits AgentAttributeSet, EnvironmentAttributeSet {
    private final String ownerName;
    private final String name;
    private final List<Attribute<SC>> attributeList;
    private final Map<String, Integer> attributeIndexMap = new HashMap<>();

    private AttributeSetLog<SC> log = null;

    private SC context = null;

    AttributeSet(String ownerName, String attributeSetName, List<Attribute<SC>> attributeList) {
        this.ownerName = ownerName;
        this.name = attributeSetName;
        this.attributeList = attributeList;
        for (int i = 0; i < this.attributeList.size(); i++) {
            Attribute<?> attribute = this.attributeList.get(i);
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

        for (Attribute<SC> attribute : attributeList)
            attribute.setContext(context);

        this.context = context;
    }


    private Attribute<C> get(int attributeIndex) {
        // noinspection unchecked
        Attribute<C> attribute = (Attribute<C>) attributeList.get(attributeIndex);
        if (attribute.accessLevel() == AttributeAccessLevel.PUBLIC)
            return attribute;
        throw new AttributeAccessException(attribute.name() + " is a PRIVATE attribute and cannot be returned.");
    }

    private Attribute<C> get(String attributeName) {
        // noinspection unchecked
        Attribute<C> attribute = (Attribute<C>) attributeList.get(attributeIndexMap.get(attributeName));
        if (attribute.accessLevel() == AttributeAccessLevel.PUBLIC)
            return attribute;
        throw new AttributeAccessException(attribute.name() + " is a PRIVATE attribute and cannot be returned.");
    }

    Event<C> getEvent(int eventIndex) {
        Attribute<C> attribute = get(eventIndex);

        if (attribute instanceof Event<C> event)
            return event;

        throw new AttributeAccessException("Expected an Event, but got: " + attribute.getClass().getName());
    }

    Event<C> getEvent(String eventName) {
        return getEvent(attributeIndexMap.get(eventName));
    }

    Routine<C> getRoutine(int routineIndex) {
        Attribute<C> attribute = get(routineIndex);

        if (attribute instanceof Routine<C> routine)
            return routine;

        throw new AttributeAccessException("Expected a Routine, but got: " + attribute.getClass().getName());
    }

    Routine<C> getRoutine(String routineName) {
        return getRoutine(attributeIndexMap.get(routineName));
    }

    Property<?,C> getProperty(int propertyIndex) {
        Attribute<C> attribute = get(propertyIndex);

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
        for (Attribute<SC> attribute : attributeList) {
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
