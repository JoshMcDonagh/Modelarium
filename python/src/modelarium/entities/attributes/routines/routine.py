from abc import ABC
from typing import override

import jpype

from modelarium.entities.attributes.attribute import Attribute
from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from modelarium.entities.attributes.routines.read_only_routine import ReadOnlyRoutine


class Routine(ABC, Attribute):
    def __init__(
            self,
            name: str,
            access_level: AttributeAccessLevel,
            java_class_name: str,
            java_run_logic_interface_name: str
    ) -> None:
        run_logic = jpype.JProxy(java_run_logic_interface_name, dict={"run": self._java_run})
        super().__init__(jpype.JClass(java_class_name)(
            name,
            access_level._java_object,
            run_logic
        ))

    @override
    def get_as_immutable(self) -> ReadOnlyRoutine:
        return ReadOnlyRoutine(self._java_object.getAsImmutable())
