from enum import Enum

import jpype

from python.src.modelarium.wrapper_object import WrapperObject


class AttributeAccessLevel(Enum, WrapperObject):
    PUBLIC = "public"
    PRIVATE = "private"

    def __init__(self) -> None:
        super().__init__(jpype.JClass("modelarium.entities.attributes.AttributeAccessLevel"))

    @property
    def _java_version(self) -> jpype.JClass:
        if self is AttributeAccessLevel.PUBLIC:
            return self._java_object.PUBLIC

        if self is AttributeAccessLevel.PRIVATE:
            return self._java_object.PRIVATE

        raise ValueError(f"Attribute access level {self} is not supported")
