from __future__ import annotations

from typing import TYPE_CHECKING

from modelarium._java.wrapper.protected_constructor_wrapper_object import ProtectedConstructorWrapperObject

if TYPE_CHECKING:
    from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel


class ReadOnlyAttribute(ProtectedConstructorWrapperObject):
    @property
    def name(self) -> str:
        return self._java_object.name()

    @property
    def is_logged(self) -> bool:
        return self._java_object.isLogged()

    @property
    def access_level(self) -> AttributeAccessLevel:
        from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
        from modelarium._java.wrapper import to_python
        return to_python(AttributeAccessLevel, self._java_object.accessLevel())
