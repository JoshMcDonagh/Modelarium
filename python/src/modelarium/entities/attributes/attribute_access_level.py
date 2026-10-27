from __future__ import annotations

from enum import Enum

from modelarium._java import java_class
from modelarium._java.wrapper.wrapper_object import WrapperObject


class AttributeAccessLevel(WrapperObject, Enum):
    PUBLIC = "public"
    PRIVATE = "private"

    def __init__(self, value: str) -> None:
        java_enum = java_class("modelarium.entities.attributes.AttributeAccessLevel")
        super().__init__(getattr(java_enum, value.upper()))
