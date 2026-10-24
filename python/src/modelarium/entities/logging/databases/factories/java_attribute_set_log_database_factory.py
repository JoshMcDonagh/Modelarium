from abc import ABC

from modelarium.entities.logging.databases.factories.attribute_set_log_database_factory import \
    AttributeSetLogDatabaseFactory
from modelarium.wrapper_object import WrapperObject


class JavaAttributeSetLogDatabaseFactory(ABC, AttributeSetLogDatabaseFactory):
    def __init__(self, java_obj: object) -> None:
        WrapperObject.__init__(self, java_obj)
