from enum import Enum
import jpype

from python.src.modelarium.wrapper_object import WrapperObject


class AttributeAccessLevel(WrapperObject, Enum):
    PUBLIC = "public"
    PRIVATE = "private"

    def __init__(self, value: str) -> None:
        java_enum = jpype.JClass("modelarium.entities.attributes.AttributeAccessLevel")
        super().__init__(getattr(java_enum, value.upper()))

    @staticmethod
    def make_python_version(java_version: object) -> "AttributeAccessLevel":
        java_enum = jpype.JClass("modelarium.entities.attributes.AttributeAccessLevel")

        if java_version == java_enum.PUBLIC:
            return AttributeAccessLevel.PUBLIC
        if java_version == java_enum.PRIVATE:

            return AttributeAccessLevel.PRIVATE

        raise ValueError(f"Java attribute access level {java_version} is not supported")
