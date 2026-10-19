from abc import ABC

from python.src.modelarium.entities.logging.databases.factories.attribute_set_log_database_factory import \
    AttributeSetLogDatabaseFactory


class JavaAttributeSetLogDatabaseFactory(ABC, AttributeSetLogDatabaseFactory):
    def __init__(self, java_obj: object) -> None:
        super().__init__()
        super()._java_obj = java_obj
