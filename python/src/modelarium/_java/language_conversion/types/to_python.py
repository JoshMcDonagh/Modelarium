from collections.abc import Iterable
from datetime import timedelta
from pathlib import Path
from typing import cast, Any, TypeVar, get_args

from modelarium._java.language_conversion import PYTHON_OBJECT_T


def _java_duration_to_python_timedelta(java_duration: object) -> timedelta:
    seconds = int(cast(Any, java_duration).getSeconds())
    nanoseconds = int(cast(Any, java_duration).getNano())

    return timedelta(seconds=seconds, microseconds=nanoseconds // 1_000)


_PYTHON_LIST_VALUE_T = TypeVar("_PYTHON_LIST_VALUE_T")


def _java_list_to_python_list(list_type: type[list[_PYTHON_LIST_VALUE_T]], java_list: Iterable) -> list:
    from modelarium._java import to_python
    item_type = get_args(list_type)[0]
    python_list = []
    for item in java_list:
        python_list.append(to_python(item_type, item))
    return python_list


_PYTHON_DICT_KEY_T = TypeVar("_PYTHON_DICT_KEY_T")
_PYTHON_DICT_VALUE_T = TypeVar("_PYTHON_DICT_VALUE_T")


def _java_map_to_python_dict(
        dict_type: type[dict[_PYTHON_DICT_KEY_T, _PYTHON_DICT_VALUE_T]],
        java_map: object
) -> dict[_PYTHON_DICT_KEY_T, _PYTHON_DICT_VALUE_T]:
    from modelarium._java import to_python
    key_type, value_type = get_args(dict_type)
    python_dict ={}
    for key, value in cast(Any, java_map).items():
        python_dict[to_python(key_type, key)] = to_python(value_type, value)
    return python_dict


def _java_path_to_python_path(java_path: object) -> Path:
    return Path(str(java_path))


def to_defined_python_type(python_type: type[PYTHON_OBJECT_T], java_object: object) -> PYTHON_OBJECT_T:
    if python_type is timedelta:
        return _java_duration_to_python_timedelta(java_object)

    if python_type is list:
        return _java_list_to_python_list(python_type, cast(Iterable, java_object))

    if python_type is dict:
        return _java_map_to_python_dict(python_type, java_object)

    if python_type is Path:
        return _java_path_to_python_path(java_object)

    raise TypeError(f"{python_type} is not a supported Python type")
