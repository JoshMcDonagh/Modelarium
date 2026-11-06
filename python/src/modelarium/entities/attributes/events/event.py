from __future__ import annotations

from abc import abstractmethod
from typing import TYPE_CHECKING
from typing_extensions import override

import jpype

from modelarium.entities.attributes.attribute import Attribute

if TYPE_CHECKING:
    from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
    from modelarium.entities.attributes.events.read_only_event import ReadOnlyEvent
    from modelarium.entities.contexts.context import Context

class Event(Attribute):
    def __init__(
            self,
            name: str,
            is_logged: bool,
            access_level: AttributeAccessLevel,
            java_class_name: str,
            java_run_logic_interface_name: str,
            java_trigger_logic_interface_name: str
    ) -> None:
        run_logic = jpype.JProxy(java_run_logic_interface_name, dict={"run": self._java_run})
        trigger_logic = jpype.JProxy(java_trigger_logic_interface_name, dict={"isTriggered": self._java_is_triggered})

        from modelarium._java import java_class
        from modelarium._java.wrapper.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_class(java_class_name)(
            name,
            is_logged,
            access_level._java_object,
            run_logic,
            trigger_logic
        ))

    def __str__(self) -> str:
        return self._java_object.toString()

    def is_triggered(self) -> bool:
        return self._java_object.isTriggered()

    @abstractmethod
    def _java_is_triggered(self, java_context: object) -> bool:
        pass

    @abstractmethod
    def _is_triggered(self, context: Context) -> bool:
        pass

    @override
    def get_as_immutable(self) -> ReadOnlyEvent:
        from modelarium._java.wrapper import to_python
        from modelarium.entities.attributes.events.read_only_event import ReadOnlyEvent
        return to_python(ReadOnlyEvent, self._java_object.getAsImmutable())
