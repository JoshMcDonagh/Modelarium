from modelarium.entities.logging.databases.java_attribute_set_log_database import JavaAttributeSetLogDatabase
from modelarium.wrapper_object import WrapperObject


class MemoryBasedAttributeSetLogDatabase(JavaAttributeSetLogDatabase):
    def __init__(self) -> None:
        super().__init__("modelarium.entities.logging.databases.MemoryBasedAttributeSetLogDatabase")


class _MemoryBasedAttributeSetLogDatabase(MemoryBasedAttributeSetLogDatabase):
    def __init__(self, java_object: object) -> None:
        WrapperObject.__init__(self, java_object)
