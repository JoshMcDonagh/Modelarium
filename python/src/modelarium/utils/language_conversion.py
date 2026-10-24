from collections.abc import Sequence, Mapping, Iterable
from datetime import timedelta

import jpype
import pandas as pd

from modelarium.wrapper_object import WrapperObject


def _timedelta_to_java_duration(duration: timedelta) -> object:
    # Convert exactly to microseconds without floating-point arithmetic.
    total_microseconds = (duration.days * 86400 + duration.seconds) * 1_000_000 + duration.microseconds

    seconds, microseconds = divmod(total_microseconds, 1_000_000)

    return jpype.JClass("java.time.Duration").ofSeconds(jpype.JLong(seconds), jpype.JLong(microseconds * 1000))


def _java_duration_to_timedelta(duration) -> timedelta:
    seconds = int(duration.getSeconds())
    nanoseconds = int(duration.getNano())

    return timedelta(seconds=seconds, microseconds=nanoseconds // 1_000)


def _list_to_java_array_list(python_list: list) -> Iterable:
    java_array_list = jpype.JClass("java.util.ArrayList")()
    for item in python_list:
        if isinstance(item, WrapperObject):
            java_array_list.add(item._java_object)
        else:
            java_array_list.add(item)
    return java_array_list


def _java_list_to_list(java_list: Iterable, item_type: type | None = None) -> list:
    python_list = []
    for item in java_list:
        if item_type is not None:
            python_list.append(item_type(item))
        else:
            python_list.append(item)
    return list(python_list)


def _java_map_to_dataframe(java_map: object, column_names: Sequence[str]) -> pd.DataFrame:
    def flatten(mapping, prefix=()):
        result = {}

        for key, value in mapping.items():
            path = prefix + (str(key),)

            if isinstance(value, Mapping) or hasattr(value, "entrySet"):
                result.update(flatten(value, path))
            else:
                result[path] = list(value)

        return result

    data = flatten(java_map)

    if not data:
        return pd.DataFrame()

    if any(len(key) != len(column_names) for key in data):
        raise ValueError("Number of column names must match the nesting depth.")

    df = pd.DataFrame(data)

    if len(column_names) > 1:
        df.columns = pd.MultiIndex.from_tuples(df.columns, names=column_names)
    else:
        df.columns.name = column_names[0]

    return df
