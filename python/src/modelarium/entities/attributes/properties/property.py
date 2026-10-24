from abc import ABC, abstractmethod
from typing import Generic, override, Type, cast

import jpype

from modelarium._java import java_class
from modelarium.entities.attributes.attribute import Attribute
from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from modelarium.entities.attributes.properties import PROPERTY_T, _type_to_string, _string_to_type, _python_to_java, \
    _java_to_python
from modelarium.entities.attributes.properties.read_only_property import ReadOnlyProperty
from modelarium.entities.contexts.context import Context


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
        java_python_type = jpype.JProxy("modelarium.python.PythonObject", inst=stored_type, convert=True)
        getter = jpype.JProxy(java_getter_interface_name, dict={"get": self._java_get})
        setter = jpype.JProxy(java_setter_interface_name, dict={"set": self._java_set})
        run_logic = jpype.JProxy(java_run_logic_interface_name, dict={"run": self._java_run})

        super().__init__(java_class(java_class_name)(
            name,
            is_logged,
            access_level._java_object,
            java_python_type,
            getter,
            setter,
            run_logic,
            _type_to_string(stored_type)
        ))

    def set(self, value: PROPERTY_T | None) -> None:
        self._java_object.set(_python_to_java(self.type, value))

    def get(self) -> PROPERTY_T | None:
        return _java_to_python(self.type, self._java_object.get())

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
        return ReadOnlyProperty[PROPERTY_T](self._java_object.getAsImmutable())
