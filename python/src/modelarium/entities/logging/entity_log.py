from modelarium._java.wrapper.protected_constructor_wrapper_object import ProtectedConstructorWrapperObject
from modelarium.entities.logging.attribute_set_log import AttributeSetLog


class EntityLog(ProtectedConstructorWrapperObject):
    def get_entity_name(self) -> str:
        return self._java_object.getEntityName()

    def get(self, attribute_set_id: int | str) -> AttributeSetLog:
        return AttributeSetLog(self._java_object.get(attribute_set_id))

    @property
    def attribute_set_log_count(self) -> int:
        return self._java_object.attributeSetLogCount()

    def disconnect_databases(self) -> None:
        self._java_object.disconnectDatabases()
