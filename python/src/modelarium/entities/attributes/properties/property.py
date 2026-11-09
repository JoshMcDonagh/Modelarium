from __future__ import annotations

from abc import abstractmethod
from typing import Generic, override, Type, TYPE_CHECKING

import jpype

from modelarium._java.wrapper import to_java
from modelarium.entities.attributes.attribute import Attribute
from modelarium.entities.attributes.properties import PROPERTY_T

if TYPE_CHECKING:
    from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
    from modelarium.entities.contexts.context import Context
    from modelarium.entities.attributes.properties.read_only_property import ReadOnlyProperty


class Property(Attribute, Generic[PROPERTY_T]):
    def __init__(
            self,
            name: str,
            is_logged: bool,
            access_level: AttributeAccessLevel,
            stored_type: Type[PROPERTY_T],
            java_class_name: str,
            java_getter_interface_name: str,
            java_setter_interface_name: str,
            java_run_logic_interface_name: str
    ) -> None:
        java_python_type = jpype.JProxy("modelarium.python.PythonObject", inst=stored_type, convert=True)
        getter = jpype.JProxy(java_getter_interface_name, dict={"get": self._java_get})
        setter = jpype.JProxy(java_setter_interface_name, dict={"set": self._java_set})
        run_logic = jpype.JProxy(java_run_logic_interface_name, dict={"run": self._java_run})

        from modelarium._java import java_class
        from modelarium._java.wrapper.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_class(java_class_name)(
            name,
            is_logged,
            to_java(access_level),
            getter,
            setter,
            run_logic,
            java_python_type
        ))

    def set(self, value: PROPERTY_T | None) -> None:
        from modelarium._java.entities.attributes.properties import python_to_java
        self._java_object.set(python_to_java(self.type, value))

    def get(self) -> PROPERTY_T | None:
        from modelarium._java.entities.attributes.properties import java_to_python
        return java_to_python(self.type, self._java_object.get())

    @property
    def type(self) -> Type[PROPERTY_T]:
        return self._java_object.getPythonType()

    @abstractmethod
    def _java_set(self, java_context: object, java_current_value: object, java_new_value: object) -> object:
        pass

    @abstractmethod
    def _set(self, context: Context, current_value: PROPERTY_T | None, new_value: PROPERTY_T | None) -> PROPERTY_T | None:
        pass

    @abstractmethod
    def _java_get(self, java_context: object, java_current_value: object) -> object:
        pass

    @abstractmethod
    def _get(self, context: Context, current_value: PROPERTY_T | None) -> PROPERTY_T | None:
        pass

    @override
    @abstractmethod
    def _java_run(self, java_context: object, java_current_value: object) -> object:
        pass

    @override
    def _run(self, context: Context, current_value: PROPERTY_T | None) -> PROPERTY_T | None:
        pass

    @override
    def get_as_immutable(self) -> ReadOnlyProperty:
        from modelarium._java.wrapper import to_python
        from modelarium.entities.attributes.properties.read_only_property import ReadOnlyProperty
        return to_python(ReadOnlyProperty[PROPERTY_T], self._java_object.getAsImmutable())
