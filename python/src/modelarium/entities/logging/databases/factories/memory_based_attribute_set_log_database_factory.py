from typing import override

from modelarium._java.wrapper import to_python
from modelarium.entities.logging.databases.factories.java_attribute_set_log_database_factory import \
    JavaAttributeSetLogDatabaseFactory
from modelarium.entities.logging.databases.memory_based_attribute_set_log_database import \
    MemoryBasedAttributeSetLogDatabase


class MemoryBasedAttributeSetLogDatabaseFactory(JavaAttributeSetLogDatabaseFactory):
    def __init__(self) -> None:
        super().__init__("modelarium.entities.logging.databases.factories.MemoryBasedAttributeSetLogDatabaseFactory")

    @override
    def create(self) -> MemoryBasedAttributeSetLogDatabase:
        return to_python(MemoryBasedAttributeSetLogDatabase, self._java_object.create())
