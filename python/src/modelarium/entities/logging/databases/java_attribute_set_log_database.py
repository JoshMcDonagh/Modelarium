from typing import override

from modelarium.entities.logging.databases.attribute_set_log_database import AttributeSetLogDatabase


class JavaAttributeSetLogDatabase(AttributeSetLogDatabase):
    def __init__(self, java_obj: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
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
        from modelarium._java import to_java
        self._java_object.setAttributeColumn(attribute_name, to_java(attribute_values))

    @override
    def get_attribute_column_as_list(self, attribute_name: str) -> list[object]:
        from modelarium._java import to_python
        return to_python(list[object], self._java_object.getAttributeColumnAsList(attribute_name))
