from __future__ import annotations

from enum import Enum
from typing import cast

from modelarium._java import java_class
from modelarium._java.wrapper.wrapper_object import WrapperObject


class AttributeAccessLevel(WrapperObject, Enum):
    PUBLIC = "public"
    PRIVATE = "private"

    def __init__(self, value: str) -> None:
        java_enum = java_class("modelarium.entities.attributes.AttributeAccessLevel")
        super().__init__(getattr(java_enum, value.upper()))

    @staticmethod
    def make_python_version(java_version: object) -> AttributeAccessLevel:
        java_enum = java_class("modelarium.entities.attributes.AttributeAccessLevel")

        if java_version == java_enum.PUBLIC:
            return cast(AttributeAccessLevel, cast(object, AttributeAccessLevel.PUBLIC))
        if java_version == java_enum.PRIVATE:
            return cast(AttributeAccessLevel, cast(object, AttributeAccessLevel.PRIVATE))

        raise ValueError(f"Java attribute access level {java_version!r} is not supported")
