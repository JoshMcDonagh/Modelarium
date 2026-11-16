from collections.abc import Iterable
from datetime import timedelta
from pathlib import Path
from typing import cast, Any, TypeVar

from modelarium._java.language_conversion import PYTHON_OBJECT_T


def java_duration_to_python_timedelta(java_duration: object) -> timedelta:
    seconds = int(cast(Any, java_duration).getSeconds())
    nanoseconds = int(cast(Any, java_duration).getNano())

    return timedelta(seconds=seconds, microseconds=nanoseconds // 1_000)


def java_list_to_python_list(java_list: Iterable, item_type: type[PYTHON_OBJECT_T]) -> list:
    from modelarium._java import to_python
    python_list = []
    for item in java_list:
        python_list.append(to_python(item_type, item))
    return python_list


PYTHON_DICT_KEY_T = TypeVar("PYTHON_DICT_KEY_T")

# TODO: Finish this...
'''
def java_map_to_python_dict(
        java_map: object,
        dict_type: type[dict[PYTHON_DICT_KEY_T, PYTHON_OBJECT_T]]
) -> dict[PYTHON_DICT_KEY_T, PYTHON_OBJECT_T]:
    from modelarium._java import to_python
    python_dict ={}
    for key, value in cast(Any, java_map).items():
        python_dict[to_python(, key)] = to_python(value_type, value)
    return python_dict
'''

def java_path_to_python_path(java_path: object) -> Path:
    return Path(str(java_path))



