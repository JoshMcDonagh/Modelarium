from modelarium._java import java_class
from modelarium.entities.logging.databases.java_attribute_set_log_database import JavaAttributeSetLogDatabase


class DiskBasedAttributeSetLogDatabase(JavaAttributeSetLogDatabase):
    def __init__(self) -> None:
        super().__init__(java_class("modelarium.entities.logging.databases.DiskBasedAttributeSetLogDatabase")())
