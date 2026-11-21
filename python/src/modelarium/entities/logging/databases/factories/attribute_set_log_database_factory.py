from __future__ import annotations

from abc import ABC, abstractmethod
from typing import TYPE_CHECKING

import jpype

from modelarium._java.objects.wrapper_object import WrapperObject

if TYPE_CHECKING:
    from modelarium.entities.logging.databases.attribute_set_log_database import AttributeSetLogDatabase


class AttributeSetLogDatabaseFactory(ABC, WrapperObject):
    def __init__(self):
        from modelarium._java import java_class
        java_cls = java_class(
            "modelarium.entities.logging.databases.functional.factories"
            ".FunctionalAttributeSetLogDatabaseFactory"
        )
        create_logic = jpype.JProxy("java.util.function.Supplier", dict={"get": self._java_create})
        super().__init__(java_cls(create_logic))

    def _java_create(self) -> object:
        from modelarium._java import to_java
        return to_java(self.create())

    @abstractmethod
    def create(self) -> AttributeSetLogDatabase:
        pass
