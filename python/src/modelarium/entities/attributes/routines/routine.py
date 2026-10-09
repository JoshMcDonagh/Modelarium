from abc import ABC, abstractmethod
from typing import override

import jpype

from python.src.modelarium.entities.attributes.attribute import Attribute
from python.src.modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from python.src.modelarium.entities.attributes.contexts.context import Context


class Routine(ABC, Attribute):
    def __init__(
            self,
            name: str,
            access_level: AttributeAccessLevel,
            java_class_name: str,
            java_run_logic_interface_name: str
    ) -> None:
        run_logic = jpype.JProxy(java_run_logic_interface_name, dict={"run": self.run})
        super().__init__(jpype.JClass(java_class_name)(
            name,
            access_level._java_object,
            run_logic
        ))

    @abstractmethod
    def run(self, context: Context) -> None:
        pass

    @override
    def get_as_immutable(self) -> object: # TODO: Update with ReadOnlyRoutine type hint
        return self._java_object.getAsImmutable()
