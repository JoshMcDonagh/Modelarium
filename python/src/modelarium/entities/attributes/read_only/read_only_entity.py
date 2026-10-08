from abc import ABC, abstractmethod

from python.src.modelarium.wrapper_object import WrapperObject


class ReadOnlyEntity(ABC, WrapperObject):
    def name(self) -> str:
        return self._java_object.name()

    def attribute_set_count(self) -> int:
        return self._java_object.attributeSetCount()

    def attribute_count(self) -> int:
        return self._java_object.attributeCount()

    @abstractmethod
    def get_attribute_set(self, attribute_set_identifier: int | str) -> object: # TODO: Update ReadOnlyAttributeSet type hint
        pass

    def get_log(self) -> object: # TODO: Update EntityLog type hint
        return self._java_object.getLog()
