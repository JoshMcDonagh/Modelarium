from collections.abc import Iterable
from datetime import timedelta
from pathlib import Path

import jpype

from modelarium._java.language_conversion import PYTHON_OBJECT_T


def _python_timedelta_to_java_duration(python_timedelta: timedelta) -> object:
    total_microseconds = (
            (python_timedelta.days * 86400 + python_timedelta.seconds)
            * 1_000_000
            + python_timedelta.microseconds
    )
    seconds, microseconds = divmod(total_microseconds, 1_000_000)

    from modelarium._java import java_class
    return java_class("java.time.Duration").ofSeconds(jpype.JLong(seconds), jpype.JLong(microseconds * 1000))


def _python_list_to_java_array_list(python_list: list) -> Iterable:
    from modelarium._java import java_class
    java_array_list = java_class("java.util.ArrayList")()
    from modelarium._java import to_java
    for item in python_list:
        java_array_list.add(to_java(item))
    return java_array_list


def _python_path_to_java_path(python_path: Path) -> object:
    from modelarium._java import java_class
    return java_class("java.nio.file.Path").of(str(python_path))


def to_defined_java_type(python_object: PYTHON_OBJECT_T) -> object:
    if isinstance(python_object, timedelta):
        return _python_timedelta_to_java_duration(python_object)

    if isinstance(python_object, list):
        return _python_list_to_java_array_list(python_object)

    if isinstance(python_object, Path):
        return _python_path_to_java_path(python_object)

    raise TypeError(f"Python object of type {type(python_object).__name__} is not supported")
