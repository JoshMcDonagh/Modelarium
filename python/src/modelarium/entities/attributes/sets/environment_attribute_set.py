from typing import override

from python.src.modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
from python.src.modelarium.entities.attributes.events.environment_event import _EnvironmentEvent, EnvironmentEvent
from python.src.modelarium.entities.attributes.properties.environment_property import _EnvironmentProperty, \
    EnvironmentProperty
from python.src.modelarium.entities.attributes.routines.environment_routine import _EnvironmentRoutine, \
    EnvironmentRoutine
from python.src.modelarium.entities.attributes.sets import _convert_to_python_environment_attribute
from python.src.modelarium.entities.attributes.sets.attribute_set import AttributeSet


class EnvironmentAttributeSet(AttributeSet):
    def __init__(self, name: str, attributes: list[EnvironmentAttribute]):
        super().__init__(name, attributes, "modelarium.entities.attributes.sets.EnvironmentAttributeSet")
    
    @override
    def get(self, attribute_id: int | str) -> EnvironmentAttribute:
        return _convert_to_python_environment_attribute(self._java_object.get(attribute_id))

    @override
    def get_event(self, event_id: int | str) -> EnvironmentEvent:
        return _EnvironmentEvent(self._java_object.getEvent(event_id))

    @override
    def get_routine(self, routine_id: int | str) -> EnvironmentRoutine:
        return _EnvironmentRoutine(self._java_object.getRoutine(routine_id))

    @override
    def get_property(self, property_id: int | str) -> EnvironmentProperty:
        return _EnvironmentProperty(self._java_object.getProperty(property_id))


class _EnvironmentAttributeSet(EnvironmentAttributeSet):
    def __init__(self, java_object: object) -> None:
        super().__init__(None, None)
        self._java_obj = java_object
