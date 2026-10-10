from typing import override

import jpype

from python.src.modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
from python.src.modelarium.entities.attributes.events.environment_event import _EnvironmentEvent, EnvironmentEvent
from python.src.modelarium.entities.attributes.properties.environment_property import _EnvironmentProperty, \
    EnvironmentProperty
from python.src.modelarium.entities.attributes.routines.environment_routine import _EnvironmentRoutine, \
    EnvironmentRoutine
from python.src.modelarium.entities.attributes.sets.attribute_set import AttributeSet


class EnvironmentAttributeSet(AttributeSet):
    def __init__(self, name: str, attributes: list[EnvironmentAttribute]):
        super().__init__(name, attributes, "modelarium.entities.attributes.sets.EnvironmentAttributeSet")
    
    @override
    def get(self, attribute_id: int | str) -> EnvironmentAttribute:
        java_attribute = self._java_object.get(attribute_id)

        if isinstance(
                java_attribute,
                jpype.JClass("modelarium.entities.attributes.events.functional.FunctionalEnvironmentEvent")
        ):
            return _EnvironmentEvent(java_attribute)

        if isinstance(
                java_attribute,
                jpype.JClass("modelarium.entities.attributes.routines.functional.FunctionalEnvironmentRoutine")
        ):
            return _EnvironmentRoutine(java_attribute)

        if isinstance(
                java_attribute,
                jpype.JClass(
                    "modelarium.entities.attributes.properties.functional.python.PythonFunctionalEnvironmentProperty")
        ):
            return _EnvironmentProperty(java_attribute)

        raise TypeError(f"Unsupported Java attribute type: {java_attribute.getClass().getName()}")

    @override
    def get_event(self, event_id: int | str) -> EnvironmentEvent:
        return _EnvironmentEvent(self._java_object.getEvent(event_id))

    @override
    def get_routine(self, routine_id: int | str) -> EnvironmentRoutine:
        return _EnvironmentRoutine(self._java_object.getRoutine(routine_id))

    @override
    def get_property(self, property_id: int | str) -> EnvironmentProperty:
        return _EnvironmentProperty(self._java_object.getProperty(property_id))
