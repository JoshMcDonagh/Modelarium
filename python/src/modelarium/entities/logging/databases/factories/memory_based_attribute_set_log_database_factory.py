from typing import override

from python.src.modelarium.entities.logging.databases.factories.java_attribute_set_log_database_factory import \
    JavaAttributeSetLogDatabaseFactory
from python.src.modelarium.entities.logging.databases.memory_based_attribute_set_log_database import \
    MemoryBasedAttributeSetLogDatabase, _MemoryBasedAttributeSetLogDatabase


class MemoryBasedAttributeSetLogDatabaseFactory(JavaAttributeSetLogDatabaseFactory):
    def __init__(self) -> None:
        super().__init__("modelarium.entities.logging.databases.factories.MemoryBasedAttributeSetLogDatabaseFactory")

    @override
    def create(self) -> MemoryBasedAttributeSetLogDatabase:
        return _MemoryBasedAttributeSetLogDatabase(self._java_object.create())
