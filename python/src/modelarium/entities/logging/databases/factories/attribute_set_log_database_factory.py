from abc import ABC, abstractmethod

import jpype

from python.src.modelarium.entities.logging.databases.attribute_set_log_database import AttributeSetLogDatabase
from python.src.modelarium.wrapper_object import WrapperObject


class AttributeSetLogDatabaseFactory(ABC, WrapperObject):
    def __init__(self):
        java_class = jpype.JClass(
            "modelarium.entities.logging.databases.functional.factories"
            ".FunctionalAttributeSetLogDatabaseFactory"
        )
        create_logic = jpype.JProxy("java.util.function.Supplier", dict={"create": self._java_create})
        super().__init__(java_class(create_logic))

    def _java_create(self) -> object:
        return self.create()._java_object

    @abstractmethod
    def create(self) -> AttributeSetLogDatabase:
        pass
