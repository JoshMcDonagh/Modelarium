from abc import ABC, abstractmethod

import jpype

from modelarium._java import java_class
from modelarium.entities.attributes.attribute import Attribute
from modelarium.entities.attributes.events.event import Event
from modelarium.entities.attributes.properties.property import Property
from modelarium.entities.attributes.routines.routine import Routine
from modelarium.entities.logging.attribute_set_log import AttributeSetLog
from modelarium._java.conversion import list_to_java_array_list
from modelarium.wrapper_object import WrapperObject


class AttributeSet(ABC, WrapperObject):
    def __init__(
            self,
            name: str,
            attributes: list[Attribute],
            java_class_name: str
    ) -> None:
        java_attributes = java_class("java.util.ArrayList")()
        for attribute in attributes:
            java_attributes.add(attribute._java_object)

        super().__init__(java_class(java_class_name)(
            name,
            list_to_java_array_list(attributes)
        ))

    @property
    def name(self) -> str:
        return self._java_object.name()

    @property
    def size(self) -> int:
        return self._java_object.size()

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

    def get_log(self) -> AttributeSetLog:
        return AttributeSetLog(self._java_object.getLog())
