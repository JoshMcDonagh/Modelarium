from abc import ABC, abstractmethod

from python.src.modelarium.entities.attributes.attribute import Attribute
from python.src.modelarium.entities.attributes.events.event import Event
from python.src.modelarium.entities.attributes.properties.property import Property
from python.src.modelarium.entities.attributes.routines.routine import Routine
from python.src.modelarium.wrapper_object import WrapperObject


class ReadOnlyAttributeSet(ABC, WrapperObject):
    @property
    def name(self) -> str:
        return self._java_object.name()

    @property
    def size(self) -> int:
        return self._java_object.size()

    def get_log(self) -> object: # TODO: Update with AttributeSetLog type hint
        return self._java_object.getLog()

    @abstractmethod
    def get(self, attribute_id: int | str) -> Attribute:
        pass

    @abstractmethod
    def get_event(self, event_id: int | str) -> Event:
        pass

    @abstractmethod
    def get_routine(self, routine_id: int | str) -> Routine:
        pass

    @abstractmethod
    def get_property(self, property_id: int | str) -> Property:
        pass
