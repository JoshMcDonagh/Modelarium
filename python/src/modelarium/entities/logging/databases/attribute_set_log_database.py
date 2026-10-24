from abc import ABC, abstractmethod
from collections.abc import Iterable
from typing import override

import jpype

from modelarium._java import java_class
from modelarium._java.conversion import java_list_to_list, list_to_java_array_list
from modelarium.wrapper_object import WrapperObject


class AttributeSetLogDatabase(ABC, WrapperObject):
    def __init__(self, database_path: str | None = None) -> None:
        java_cls = java_class("modelarium.entities.logging.databases.functional.FunctionalAttributeSetLogDatabase")
        connect_logic = jpype.JProxy("java.lang.Runnable", dict={"run": self.connect})
        disconnect_logic = jpype.JProxy("java.lang.Runnable", dict={"run": self.disconnect})
        add_attribute_value_logic = jpype.JProxy(
            "java.util.function.BiConsumer", dict={"accept": self._java_add_attribute_value}
        )
        set_attribute_column_logic = jpype.JProxy(
            "java.util.function.BiConsumer", dict={"accept": self._java_set_attribute_column}
        )
        get_attribute_value_logic = jpype.JProxy(
            "java.util.function.Function", dict={"apply": self._java_get_attribute_column_as_list}
        )

        if database_path is None:
            super().__init__(java_cls(
                connect_logic,
                disconnect_logic,
                add_attribute_value_logic,
                set_attribute_column_logic,
                get_attribute_value_logic
            ))
        else:
            super().__init__(java_cls(
                database_path,
                connect_logic,
                disconnect_logic,
                add_attribute_value_logic,
                set_attribute_column_logic,
                get_attribute_value_logic
            ))

    def get_database_path(self) -> str:
        return self._java_object.getDatabasePath()

    @abstractmethod
    def connect(self) -> None:
        pass

    @abstractmethod
    def disconnect(self) -> None:
        pass

    @abstractmethod
    def add_attribute_value(self, attribute_name: str, attribute_value: object) -> None:
        pass

    def _java_set_attribute_column(self, attribute_name: str, java_attribute_values: Iterable[object]) -> None:
        self.set_attribute_column(attribute_name, java_list_to_list(java_attribute_values))

    @abstractmethod
    def set_attribute_column(self, attribute_name: str, attribute_values: list[object]) -> None:
        pass

    def _java_get_attribute_column_as_list(self, attribute_name: str) -> Iterable[object]:
        return list_to_java_array_list(self.get_attribute_column_as_list(attribute_name))

    @abstractmethod
    def get_attribute_column_as_list(self, attribute_name: str) -> list[object]:
        pass


class _AttributeSetLogDatabase(AttributeSetLogDatabase):
    def __init__(self, java_object: object) -> None:
        WrapperObject.__init__(self, java_object)

    @override
    def connect(self) -> None:
        self._java_object.connect()

    @override
    def disconnect(self) -> None:
        self._java_object.disconnect()

    @override
    def add_attribute_value(self, attribute_name: str, attribute_value: object) -> None:
        self._java_object.addAttributeValue(attribute_name, attribute_value)

    @override
    def set_attribute_column(self, attribute_name: str, attribute_values: list[object]) -> None:
        self._java_object.setAttributeColumn(attribute_name, list_to_java_array_list(attribute_values))

    @override
    def get_attribute_column_as_list(self, attribute_name: str) -> list[object]:
        return java_list_to_list(self._java_object.getAttributeColumnAsList(attribute_name))
