from collections.abc import Sequence, Mapping, Iterable
from datetime import timedelta
from pathlib import Path
from typing import cast, Any

import jpype
import pandas as pd


def java_map_to_dataframe(java_map: object, column_names: Sequence[str]) -> pd.DataFrame:
    def flatten(mapping, prefix=()):
        from modelarium._java import to_python
        result = {}
        for key, value in mapping.items():
            path = prefix + (str(key),)
            if isinstance(value, Mapping) or hasattr(value, "entrySet"):
                result.update(flatten(value, path))
            else:
                result[path] = [to_python(Any, item) for item in value]
        return result

    data = flatten(cast(Any, java_map))

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


def java_to_python_path(java_path: object) -> Path:
    return Path(str(java_path))
