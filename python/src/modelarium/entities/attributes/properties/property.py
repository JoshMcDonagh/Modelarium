from abc import ABC, abstractmethod
from typing import Generic, override

import jpype

from python.src.modelarium.entities.attributes.attribute import Attribute
from python.src.modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from python.src.modelarium.entities.attributes.properties import PROPERTY_T, _type_to_string, _string_to_type
from python.src.modelarium.entities.attributes.properties.read_only_property import ReadOnlyProperty
from python.src.modelarium.entities.contexts.context import Context


class Property(ABC, Attribute, Generic[PROPERTY_T]):
    def __init__(
            self,
            name: str,
            is_logged: bool,
            access_level: AttributeAccessLevel,
            stored_type: type[PROPERTY_T],
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
            access_level._java_object,
            jpype.JClass("java.lang.Object").class_,
            getter,
            setter,
            run_logic,
            _type_to_string(stored_type)
        ))

    def _set(self, value: PROPERTY_T) -> None:
        self._java_object.set(value)

    def _get(self) -> PROPERTY_T:
        return self._java_object.get()

    def stored_type(self) -> type[PROPERTY_T]:
        return _string_to_type(self._java_object.getPythonType())

    @abstractmethod
    def set(self, context: Context, value: PROPERTY_T) -> None:
        pass

    @abstractmethod
    def get(self, context: Context) -> PROPERTY_T:
        pass

    def run(self, context: Context) -> None:
        return

    @override
    def get_as_immutable(self) -> ReadOnlyProperty:
        return ReadOnlyProperty[PROPERTY_T](self._java_object.getAsImmutable())
