from typing import override

from modelarium._java import java_class
from modelarium._java.entities.attributes.properties.python_value_codec import create_python_value_codec
from modelarium.entities.logging.databases.disk_based_attribute_set_log_database import \
    DiskBasedAttributeSetLogDatabase, _DiskBasedAttributeSetLogDatabase
from modelarium.entities.logging.databases.factories.java_attribute_set_log_database_factory import \
    JavaAttributeSetLogDatabaseFactory


class DiskBasedAttributeSetLogDatabaseFactory(JavaAttributeSetLogDatabaseFactory):
    def __init__(self) -> None:
        self._codec = create_python_value_codec()
        java_factory = java_class(
            "modelarium.entities.logging.databases.factories."
            "DiskBasedAttributeSetLogDatabaseFactory"
        )(self._codec)

        super().__init__(java_factory)

    @override
    def create(self) -> DiskBasedAttributeSetLogDatabase:
        return _DiskBasedAttributeSetLogDatabase(self._java_object.create())
