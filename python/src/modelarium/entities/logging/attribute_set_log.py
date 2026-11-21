from modelarium._java.objects.protected_constructor_wrapper_object import ProtectedConstructorWrapperObject


class AttributeSetLog(ProtectedConstructorWrapperObject):
    def get_owner_name(self) -> str:
        return self._java_object.getOwnerName()

    def get_attribute_set_name(self) -> str:
        return self._java_object.getAttributeSetName()

    def get_attribute_names_list(self) -> list[str]:
        from modelarium._java import to_python
        return to_python(list[str], self._java_object.getAttributeNamesList(), str)

    @property
    def attribute_log_count(self) -> int:
        return self._java_object.attributeLogCount()

    @property
    def attribute_log_entry_count(self) -> int:
        return self._java_object.attributeLogEntryCount()

    def get_values(self, attribute_name: str) -> list[object]:
        from modelarium._java import to_python
        return to_python(list[str], self._java_object.getValues(attribute_name))

    def disconnect_database(self) -> None:
        self._java_object.disconnectDatabase()
