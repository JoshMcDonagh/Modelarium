from __future__ import annotations

from typing import override, TYPE_CHECKING

from modelarium._java import java_class
from modelarium._java import to_python
from modelarium._java.python_value_codec import create_python_value_codec
from modelarium.entities.logging.databases.factories.java_attribute_set_log_database_factory import \
    JavaAttributeSetLogDatabaseFactory

if TYPE_CHECKING:
    from modelarium.entities.logging.databases.disk_based_attribute_set_log_database import \
        DiskBasedAttributeSetLogDatabase


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
        from modelarium.entities.logging.databases.disk_based_attribute_set_log_database import \
            DiskBasedAttributeSetLogDatabase
        return to_python(DiskBasedAttributeSetLogDatabase, self._java_object.create())
