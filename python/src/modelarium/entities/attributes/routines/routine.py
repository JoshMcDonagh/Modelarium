from __future__ import annotations

from abc import abstractmethod
from typing import override, TYPE_CHECKING

import jpype

from modelarium._java.wrapper import to_java
from modelarium.entities.attributes.attribute import Attribute

if TYPE_CHECKING:
    from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
    from modelarium.entities.attributes.routines.read_only_routine import ReadOnlyRoutine
    from modelarium.entities.contexts.context import Context


class Routine(Attribute):
    def __init__(
            self,
            name: str,
            access_level: AttributeAccessLevel,
            java_class_name: str,
            java_run_logic_interface_name: str
    ) -> None:
        run_logic = jpype.JProxy(java_run_logic_interface_name, dict={"run": self._java_run})
        from modelarium._java import java_class
        from modelarium._java.wrapper.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_class(java_class_name)(
            name,
            to_java(access_level),
            run_logic
        ))

    @abstractmethod
    def _java_run(self, java_context: object) -> None:
        pass

    @abstractmethod
    def _run(self, context: Context) -> None:
        pass

    @override
    def get_as_immutable(self) -> ReadOnlyRoutine:
        from modelarium._java.wrapper import to_python
        from modelarium.entities.attributes.routines.read_only_routine import ReadOnlyRoutine
        return to_python(ReadOnlyRoutine, self._java_object.getAsImmutable())
