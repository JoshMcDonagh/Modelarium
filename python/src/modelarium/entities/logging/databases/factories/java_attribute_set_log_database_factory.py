from modelarium._java.wrapper.wrapper_object import WrapperObject
from modelarium.entities.logging.databases.factories.attribute_set_log_database_factory import \
    AttributeSetLogDatabaseFactory


class JavaAttributeSetLogDatabaseFactory(AttributeSetLogDatabaseFactory):
    def __init__(self, java_obj: object) -> None:
        WrapperObject.__init__(self, java_obj)
