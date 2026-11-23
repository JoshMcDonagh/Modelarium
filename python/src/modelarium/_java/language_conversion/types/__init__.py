from datetime import timedelta
from pathlib import Path
from typing import get_origin

from modelarium._java.language_conversion import PYTHON_OBJECT_T

_DEFINED_PYTHON_TYPES: list[type] = [
    timedelta,
    list,
    dict,
    Path
]


def is_defined_python_type(python_type: type[PYTHON_OBJECT_T]) -> bool:
    return (get_origin(python_type) or python_type) in _DEFINED_PYTHON_TYPES


def is_instance_of_defined_python_type(python_object: PYTHON_OBJECT_T) -> bool:
    return is_defined_python_type(type(python_object))
