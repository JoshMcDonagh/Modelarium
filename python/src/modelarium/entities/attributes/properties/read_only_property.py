from typing import Generic, override, Type

from modelarium._java.entities.attributes.properties import java_to_python
from modelarium.entities.attributes.properties import PROPERTY_T
from modelarium.entities.attributes.read_only_attribute import ReadOnlyAttribute


class ReadOnlyProperty(Generic[PROPERTY_T], ReadOnlyAttribute):
    @override
    def __str__(self) -> str:
        return self._java_object.toString()

    @property
    def type(self) -> Type[PROPERTY_T]:
        return self._java_object.getPythonType()

    def get(self) -> PROPERTY_T | None:
        return java_to_python(self.type, self._java_object.get())
