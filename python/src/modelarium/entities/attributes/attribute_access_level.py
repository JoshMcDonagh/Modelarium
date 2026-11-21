from __future__ import annotations

from enum import Enum

from modelarium._java.objects.convertible_python_object import ConvertiblePythonObject


class AttributeAccessLevel(ConvertiblePythonObject, Enum):
    PUBLIC = "public"
    PRIVATE = "private"
