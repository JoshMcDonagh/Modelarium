from python.src.modelarium.utils.language_conversion import _java_list_to_list
from python.src.modelarium.wrapper_object import WrapperObject


class AttributeSetLog(WrapperObject):
    def get_owner_name(self) -> str:
        return self._java_object.getOwnerName()

    def get_attribute_set_name(self) -> str:
        return self._java_object.getAttributeSetName()

    def get_attribute_names_list(self) -> list[str]:
        return _java_list_to_list(self._java_object.getAttributeNamesList(), str)

    @property
    def attribute_log_count(self) -> int:
        return self._java_object.attributeLogCount()

    @property
    def attribute_log_entry_count(self) -> int:
        return self._java_object.attributeLogEntryCount()

    def get_values(self, attribute_name: str) -> list[object]:
        return _java_list_to_list(self._java_object.getValues(attribute_name))

    def disconnect_database(self) -> None:
        self._java_object.disconnectDatabase()
