from abc import ABC, abstractmethod

import jpype

from python.src.modelarium.wrapper_object import WrapperObject


class Entity(ABC, WrapperObject):
    def __init__(self, java_entity: jpype.JClass):
        super().__init__(java_entity)

    @property
    def context(self): # TODO: Update to return a SimulationContext wrapper type
        return self.java_object.context()

    @property
    def name(self) -> str:
        return self.java_object.name()

    @property
    def attribute_set_count(self) -> int:
        return self.java_object.attributeSetCount()

    @property
    def attribute_count(self) -> int:
        return self.java_object.attributeCount()

    def get_attribute_set(self, attribute_set_id: int | str): # TODO: Update to return a AttributeSet wrapper type
        return self.java_object.getAttributeSet(attribute_set_id)

    def getLog(self): # TODO: Update to return an EntityLog wrapper type
        return self.java_object.getLog()

    def run(self) -> None:
        self.java_object.run()

    @abstractmethod
    def get_as_immutable(self): # TODO: Update to return a ReadOnlyEntity wrapper type
        pass
