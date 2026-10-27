from typing import override

from modelarium._java.wrapper import to_python
from modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
from modelarium.entities.attributes.events.environment_event import EnvironmentEvent
from modelarium.entities.attributes.properties.environment_property import EnvironmentProperty
from modelarium.entities.attributes.routines.environment_routine import EnvironmentRoutine
from modelarium.entities.attributes.sets.attribute_set import AttributeSet


class EnvironmentAttributeSet(AttributeSet):
    def __init__(self, name: str, attributes: list[EnvironmentAttribute]):
        super().__init__(name, attributes, "modelarium.entities.attributes.sets.EnvironmentAttributeSet")
    
    @override
    def get(self, attribute_id: int | str) -> EnvironmentAttribute:
        return to_python(EnvironmentAttribute, self._java_object.get(attribute_id))

    @override
    def get_event(self, event_id: int | str) -> EnvironmentEvent:
        return to_python(EnvironmentEvent, self._java_object.getEvent(event_id))

    @override
    def get_routine(self, routine_id: int | str) -> EnvironmentRoutine:
        return to_python(EnvironmentRoutine, self._java_object.getRoutine(routine_id))

    @override
    def get_property(self, property_id: int | str) -> EnvironmentProperty:
        return to_python(EnvironmentProperty, self._java_object.getProperty(property_id))
