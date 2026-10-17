import pandas as pd
from collections.abc import Mapping, Sequence


def _java_map_to_dataframe(
    java_map,
    column_names: Sequence[str]
) -> pd.DataFrame:

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