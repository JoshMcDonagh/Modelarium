from __future__ import annotations

from typing import override, TYPE_CHECKING

from modelarium._java import java_class
from modelarium._java.wrapper import to_python
from modelarium.entities.logging.databases.factories.java_attribute_set_log_database_factory import \
    JavaAttributeSetLogDatabaseFactory

if TYPE_CHECKING:
    from modelarium.entities.logging.databases.memory_based_attribute_set_log_database import \
        MemoryBasedAttributeSetLogDatabase


class MemoryBasedAttributeSetLogDatabaseFactory(JavaAttributeSetLogDatabaseFactory):
    def __init__(self) -> None:
        super().__init__(java_class("modelarium.entities.logging.databases.factories.MemoryBasedAttributeSetLogDatabaseFactory")())

    @override
    def create(self) -> MemoryBasedAttributeSetLogDatabase:
        from modelarium.entities.logging.databases.memory_based_attribute_set_log_database import \
            MemoryBasedAttributeSetLogDatabase
        return to_python(MemoryBasedAttributeSetLogDatabase, self._java_object.create())
