from abc import ABC, abstractmethod

import jpype
from typing_extensions import override

from python.src.modelarium.entities.attributes.attribute import Attribute
from python.src.modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
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
    ):
        run_logic = jpype.JProxy(java_run_logic_interface_name, dict={"run": self.run})
        trigger_logic = jpype.JProxy(java_trigger_logic_interface_name, dict={"is_triggered": self.is_triggered})

        super().__init__(jpype.JClass(java_class_name)(name, is_logged, access_level, run_logic, trigger_logic))

    def __str__(self) -> str:
        return self._java_object.toString()

    def _is_triggered(self) -> bool:
        return self._java_object.isTriggered()

    @override
    def _run(self) -> None:
        self._java_object.run()

    @abstractmethod
    def is_triggered(self, context: object) -> bool: # TODO: Update with context type hint
        pass

    @abstractmethod
    def run(self, context: object) -> None: # TODO: Update with context type hint
        pass

    @override
    def get_as_immutable(self) -> ReadOnlyEvent:
        return ReadOnlyEvent(self._java_object.getAsImmutable())
