from abc import ABC, abstractmethod

from python.src.modelarium.entities.attributes.events.read_only_event import ReadOnlyEvent
from python.src.modelarium.entities.attributes.properties.read_only_property import ReadOnlyProperty
from python.src.modelarium.entities.attributes.routines.read_only_routine import ReadOnlyRoutine
from python.src.modelarium.wrapper_object import WrapperObject


class ReadOnlyEntity(ABC, WrapperObject):
    def name(self) -> str:
        return self._java_object.name()

    def attribute_set_count(self) -> int:
        return self._java_object.attributeSetCount()

    def attribute_count(self) -> int:
        return self._java_object.attributeCount()

    @abstractmethod
    def get_attribute_set(self, attribute_set_identifier: int | str) -> object: # TODO: Update ReadOnlyAttributeSet type hint
        pass

    def get_log(self) -> object: # TODO: Update EntityLog type hint
        return self._java_object.getLog()

    def get_event(self, attribute_set_name: str, event_name: str) -> ReadOnlyEvent:
        return ReadOnlyEvent(self._java_object.getEvent(attribute_set_name, event_name))

    def get_routine(self, attribute_set_name: str, routine_name: str) -> ReadOnlyRoutine:
        return ReadOnlyRoutine(self._java_object.getRoutine(attribute_set_name, routine_name))

    def get_property(self, attribute_set_name: str, property_name: str) -> ReadOnlyProperty:
        return ReadOnlyProperty(self._java_object.getProperty(attribute_set_name, property_name))
