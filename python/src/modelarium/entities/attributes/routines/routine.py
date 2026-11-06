from __future__ import annotations

from typing import override, TYPE_CHECKING

import jpype

from modelarium.entities.attributes.attribute import Attribute

if TYPE_CHECKING:
    from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
    from modelarium.entities.attributes.routines.read_only_routine import ReadOnlyRoutine


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
            access_level._java_object,
            run_logic
        ))

    @override
    def get_as_immutable(self) -> ReadOnlyRoutine:
        from modelarium._java.wrapper import to_python
        from modelarium.entities.attributes.routines.read_only_routine import ReadOnlyRoutine
        return to_python(ReadOnlyRoutine, self._java_object.getAsImmutable())
