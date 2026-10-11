from python.src.modelarium.wrapper_object import WrapperObject


class AttributeSetLog(WrapperObject):
    def get_owner_name(self) -> str:
        return self._java_object.getOwnerName()

    def get_attribute_set_name(self) -> str:
        return self._java_object.getAttributeSetName()

    def get_attribute_names_list(self) -> list[str]:
        java_list = self._java_object.getAttributeNamesList()
        attribute_names_list = []
        for attribute_name in java_list:
            attribute_names_list.append(attribute_name)
        return attribute_names_list

    @property
    def attribute_log_count(self) -> int:
        return self._java_object.attributeLogCount()

    @property
    def attribute_log_entry_count(self) -> int:
        return self._java_object.attributeLogEntryCount()

    def get_values(self, attribute_name: str) -> list[object]:
        java_list = self._java_object.getValues(attribute_name)
        values_list = []
        for value in java_list:
            values_list.append(value)
        return values_list

    def disconnect_database(self) -> None:
        self._java_object.disconnectDatabase()
