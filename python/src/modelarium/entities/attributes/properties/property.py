from abc import ABC, abstractmethod
from typing import TypeVar, Generic, override

import jpype

from python.src.modelarium.entities.attributes.attribute import Attribute
from python.src.modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from python.src.modelarium.entities.attributes.contexts.context import Context

T = TypeVar("T")


class Property(ABC, Attribute, Generic[T]):
    def __init__(
            self,
            name: str,
            is_logged: bool,
            access_level: AttributeAccessLevel,
            java_class_name: str,
            java_getter_interface_name: str,
            java_setter_interface_name: str,
            java_run_logic_interface_name: str
    ) -> None:
        getter = jpype.JProxy(java_getter_interface_name, dict={"get": self.get})
        setter = jpype.JProxy(java_setter_interface_name, dict={"set": self.set})
        run_logic = jpype.JProxy(java_run_logic_interface_name, dict={"run": self.run})

        super().__init__(jpype.JClass(java_class_name)(
            name,
            is_logged,
            access_level,
            jpype.JClass("java.lang.Object").class_
        ))

    def _set(self, value: T) -> None:
        self._java_object.set(value)

    def _get(self) -> T:
        return self._java_object.get()

    @abstractmethod
    def set(self, context: Context, value: T) -> None:
        pass

    @abstractmethod
    def get(self, context: Context) -> T:
        pass

    def run(self, context: Context) -> None:
        return

    @override
    def get_as_immutable(self) -> object: # TODO: Update with ReadOnlyProperty type hint
        return self._java_object.getAsImmutable()
