from abc import ABC, abstractmethod

import jpype
from typing_extensions import override

from python.src.modelarium.entities.attributes.attribute import Attribute
from python.src.modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from python.src.modelarium.entities.attributes.contexts.context import Context
from python.src.modelarium.entities.attributes.events.read_only_event import ReadOnlyEvent


class Event(ABC, Attribute):
    def __init__(
            self,
            name: str,
            is_logged: bool,
            access_level: AttributeAccessLevel,
            java_class_name: str,
            java_run_logic_interface_name: str,
            java_trigger_logic_interface_name: str
    ) -> None:
        run_logic = jpype.JProxy(java_run_logic_interface_name, dict={"run": self.run})
        trigger_logic = jpype.JProxy(java_trigger_logic_interface_name, dict={"is_triggered": self.is_triggered})

        super().__init__(jpype.JClass(java_class_name)(
            name,
            is_logged,
            access_level._java_object,
            run_logic,
            trigger_logic
        ))

    def __str__(self) -> str:
        return self._java_object.toString()

    def _is_triggered(self) -> bool:
        return self._java_object.isTriggered()

    @abstractmethod
    def is_triggered(self, context: Context) -> bool:
        pass

    @abstractmethod
    def run(self, context: Context) -> None:
        pass

    @override
    def get_as_immutable(self) -> ReadOnlyEvent:
        return ReadOnlyEvent(self._java_object.getAsImmutable())
