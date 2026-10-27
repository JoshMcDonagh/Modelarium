from abc import ABC

from modelarium._java.wrapper.protected_constructor_wrapper_object import ProtectedConstructorWrapperObject
from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel


class ReadOnlyAttribute(ABC, ProtectedConstructorWrapperObject):
    @property
    def name(self) -> str:
        return self._java_object.name()

    @property
    def is_logged(self) -> bool:
        return self._java_object.isLogged()

    @property
    def access_level(self) -> AttributeAccessLevel:
        return AttributeAccessLevel.make_python_version(self._java_object.accessLevel())
