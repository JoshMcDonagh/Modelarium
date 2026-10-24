from typing import Generic, override, Type, get_args

from modelarium.entities.attributes.properties import _java_to_python
from modelarium.entities.attributes.properties.property import PROPERTY_T
from modelarium.entities.attributes.read_only_attribute import ReadOnlyAttribute


class ReadOnlyProperty(ReadOnlyAttribute, Generic[PROPERTY_T]):
    def __init__(self, java_obj: object) -> None:
        super().__init__(java_obj)

    @override
    def __str__(self) -> str:
        return self._java_object.toString()

    @property
    def type(self) -> Type[PROPERTY_T]:
        return self._java_object.getPythonType()

    def get(self) -> PROPERTY_T | None:
        return _java_to_python(self.type, self._java_object.get())
