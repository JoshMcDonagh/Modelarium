from abc import ABC, abstractmethod

from modelarium._java.wrapper.protected_constructor_wrapper_object import ProtectedConstructorWrapperObject
from modelarium.entities.attributes.events.read_only_event import ReadOnlyEvent
from modelarium.entities.attributes.properties.read_only_property import ReadOnlyProperty
from modelarium.entities.attributes.read_only_attribute import ReadOnlyAttribute
from modelarium.entities.attributes.routines.read_only_routine import ReadOnlyRoutine
from modelarium.entities.logging.attribute_set_log import AttributeSetLog


class ReadOnlyAttributeSet(ABC, ProtectedConstructorWrapperObject):
    @property
    def name(self) -> str:
        return self._java_object.name()

    @property
    def size(self) -> int:
        return self._java_object.size()

    def get_log(self) -> AttributeSetLog:
        return AttributeSetLog(self._java_object.getLog())

    @abstractmethod
    def get(self, attribute_id: int | str) -> ReadOnlyAttribute:
        pass

    @abstractmethod
    def get_event(self, event_id: int | str) -> ReadOnlyEvent:
        pass

    @abstractmethod
    def get_routine(self, routine_id: int | str) -> ReadOnlyRoutine:
        pass

    @abstractmethod
    def get_property(self, property_id: int | str) -> ReadOnlyProperty:
        pass
