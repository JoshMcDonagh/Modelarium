from abc import ABC, abstractmethod

from python.src.modelarium.entities.attributes.sets.attribute_set import AttributeSet
from python.src.modelarium.entities.contexts.context import Context
from python.src.modelarium.entities.logging.entity_log import EntityLog
from python.src.modelarium.entities.read_only.read_only_entity import ReadOnlyEntity
from python.src.modelarium.wrapper_object import WrapperObject


class Entity(ABC, WrapperObject):
    @property
    @abstractmethod
    def context(self) -> Context:
        pass

    @property
    def name(self) -> str:
        return self._java_object.name()

    @property
    def attribute_set_count(self) -> int:
        return self._java_object.attributeSetCount()

    @property
    def attribute_count(self) -> int:
        return self._java_object.attributeCount()

    @abstractmethod
    def get_attribute_set(self, attribute_set_id: int | str) -> AttributeSet:
        pass

    def get_log(self) -> EntityLog:
        return EntityLog(self._java_object.getLog())

    def run(self) -> None:
        self._java_object._run()

    @abstractmethod
    def get_as_immutable(self) -> ReadOnlyEntity:
        pass
