from __future__ import annotations

from abc import ABC, abstractmethod

from typing import TYPE_CHECKING

from modelarium._java.wrapper import to_python
from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from modelarium.entities.attributes.read_only_attribute import ReadOnlyAttribute
from modelarium._java.wrapper.wrapper_object import WrapperObject

if TYPE_CHECKING:
    from modelarium.entities.contexts.context import Context


class Attribute(ABC, WrapperObject):
    @property
    def name(self) -> str:
        return self._java_object.name()

    @property
    def is_logged(self) -> bool:
        return self._java_object.isLogged()

    @property
    def access_level(self) -> AttributeAccessLevel:
        return to_python(AttributeAccessLevel, self._java_object.accessLevel())

    def run(self) -> None:
        self._java_object.run()

    @abstractmethod
    def _java_run(self, java_context: object) -> None:
        pass

    @abstractmethod
    def _run(self, context: Context) -> None:
        pass

    @abstractmethod
    def get_as_immutable(self) -> ReadOnlyAttribute:
        pass
