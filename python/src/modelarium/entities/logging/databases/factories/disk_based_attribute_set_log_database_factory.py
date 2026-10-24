from typing import override

from modelarium.entities.logging.databases.disk_based_attribute_set_log_database import \
    DiskBasedAttributeSetLogDatabase, _DiskBasedAttributeSetLogDatabase
from modelarium.entities.logging.databases.factories.java_attribute_set_log_database_factory import \
    JavaAttributeSetLogDatabaseFactory


class DiskBasedAttributeSetLogDatabaseFactory(JavaAttributeSetLogDatabaseFactory):
    def __init__(self) -> None:
        super().__init__("modelarium.entities.logging.databases.factories.DiskBasedAttributeSetLogDatabaseFactory")

    @override
    def create(self) -> DiskBasedAttributeSetLogDatabase:
        return _DiskBasedAttributeSetLogDatabase(self._java_object.create())
