from abc import ABC, abstractmethod

import jpype

from modelarium._java import java_class
from modelarium._java.wrapper import to_java
from modelarium._java.wrapper.wrapper_object import WrapperObject
from modelarium.entities.logging.databases.attribute_set_log_database import AttributeSetLogDatabase


class AttributeSetLogDatabaseFactory(ABC, WrapperObject):
    def __init__(self):
        java_cls = java_class(
            "modelarium.entities.logging.databases.functional.factories"
            ".FunctionalAttributeSetLogDatabaseFactory"
        )
        create_logic = jpype.JProxy("java.util.function.Supplier", dict={"get": self._java_create})
        super().__init__(java_cls(create_logic))

    def _java_create(self) -> object:
        return to_java(self.create())

    @abstractmethod
    def create(self) -> AttributeSetLogDatabase:
        pass
