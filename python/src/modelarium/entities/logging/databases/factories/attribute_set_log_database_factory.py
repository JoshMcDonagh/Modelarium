from abc import ABC, abstractmethod
from typing import override

import jpype

from modelarium._java import java_class
from modelarium.entities.logging.databases.attribute_set_log_database import AttributeSetLogDatabase, \
    _AttributeSetLogDatabase
from modelarium.wrapper_object import WrapperObject


class AttributeSetLogDatabaseFactory(ABC, WrapperObject):
    def __init__(self):
        java_cls = java_class(
            "modelarium.entities.logging.databases.functional.factories"
            ".FunctionalAttributeSetLogDatabaseFactory"
        )
        create_logic = jpype.JProxy("java.util.function.Supplier", dict={"get": self._java_create})
        super().__init__(java_cls(create_logic))

    def _java_create(self) -> object:
        return self.create()._java_object

    @abstractmethod
    def create(self) -> AttributeSetLogDatabase:
        pass


class _AttributeSetLogDatabaseFactory(AttributeSetLogDatabaseFactory):
    def __init__(self, java_object: object) -> None:
        WrapperObject.__init__(self, java_object)

    @override
    def create(self) -> AttributeSetLogDatabase:
        return _AttributeSetLogDatabase(self._java_object.create())
