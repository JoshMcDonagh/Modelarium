from abc import ABC, abstractmethod

from modelarium._java.wrapper.protected_constructor_wrapper_object import ProtectedConstructorWrapperObject
from modelarium.entities.attributes.events.read_only_event import ReadOnlyEvent
from modelarium.entities.attributes.properties.read_only_property import ReadOnlyProperty
from modelarium.entities.attributes.routines.read_only_routine import ReadOnlyRoutine
from modelarium.entities.attributes.sets.read_only.read_only_attribute_set import ReadOnlyAttributeSet
from modelarium.entities.logging.entity_log import EntityLog


class ReadOnlyEntity(ABC, ProtectedConstructorWrapperObject):
    def name(self) -> str:
        return self._java_object.name()

    def attribute_set_count(self) -> int:
        return self._java_object.attributeSetCount()

    def attribute_count(self) -> int:
        return self._java_object.attributeCount()

    @abstractmethod
    def get_attribute_set(self, attribute_set_identifier: int | str) -> ReadOnlyAttributeSet:
        pass

    def get_log(self) -> EntityLog:
        return EntityLog(self._java_object.getLog())

    def get_event(self, attribute_set_name: str, event_name: str) -> ReadOnlyEvent:
        return ReadOnlyEvent(self._java_object.getEvent(attribute_set_name, event_name))

    def get_routine(self, attribute_set_name: str, routine_name: str) -> ReadOnlyRoutine:
        return ReadOnlyRoutine(self._java_object.getRoutine(attribute_set_name, routine_name))

    def get_property(self, attribute_set_name: str, property_name: str) -> ReadOnlyProperty:
        return ReadOnlyProperty(self._java_object.getProperty(attribute_set_name, property_name))
