from abc import ABC, abstractmethod

from modelarium.entities.attributes.attribute import Attribute
from modelarium.entities.attributes.events.event import Event
from modelarium.entities.attributes.properties.property import Property
from modelarium.entities.attributes.routines.routine import Routine
from modelarium.entities.logging.attribute_set_log import AttributeSetLog
from modelarium.wrapper_object import WrapperObject


class ReadOnlyAttributeSet(ABC, WrapperObject):
    @property
    def name(self) -> str:
        return self._java_object.name()

    @property
    def size(self) -> int:
        return self._java_object.size()

    def get_log(self) -> AttributeSetLog:
        return AttributeSetLog(self._java_object.getLog())

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
