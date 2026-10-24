from abc import ABC
from typing import override

from modelarium._java.conversion import list_to_java_array_list, java_list_to_list
from modelarium.entities.logging.databases.attribute_set_log_database import AttributeSetLogDatabase
from modelarium.wrapper_object import WrapperObject


class JavaAttributeSetLogDatabase(ABC, AttributeSetLogDatabase):
    def __init__(self, java_obj: object) -> None:
        WrapperObject.__init__(self, java_obj)

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
