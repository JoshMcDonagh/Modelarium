from abc import ABC, abstractmethod

from python.src.modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from python.src.modelarium.entities.attributes.read_only_attribute import ReadOnlyAttribute
from python.src.modelarium.wrapper_object import WrapperObject


class Attribute(ABC, WrapperObject):
    @property
    def name(self) -> str:
        return self._java_object.name()

    @property
    def is_logged(self) -> bool:
        return self._java_object.isLogged()

    @property
    def access_level(self) -> AttributeAccessLevel:
        return AttributeAccessLevel.make_python_version(self._java_object.accessLevel())

    @abstractmethod
    def _run(self) -> None:
        pass

    @abstractmethod
    def get_as_immutable(self) -> ReadOnlyAttribute:
        pass
