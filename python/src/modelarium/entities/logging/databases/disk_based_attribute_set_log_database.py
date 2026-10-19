from python.src.modelarium.entities.logging.databases.java_attribute_set_log_database import JavaAttributeSetLogDatabase


class DiskBasedAttributeSetLogDatabase(JavaAttributeSetLogDatabase):
    def __init__(self) -> None:
        super().__init__("modelarium.entities.logging.databases.DiskBasedAttributeSetLogDatabase")

class _DiskBasedAttributeSetLogDatabase(DiskBasedAttributeSetLogDatabase):
    def __init__(self, java_object: object) -> None:
        super().__init__()
        super()._java_obj = java_object
