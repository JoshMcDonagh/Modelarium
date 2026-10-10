import importlib
from typing import TypeVar

PROPERTY_T = TypeVar("PROPERTY_T")


def _type_to_string(value_type: type[PROPERTY_T]) -> str:
    return f"{value_type.__module__}.{value_type.__qualname__}"


def _string_to_type(type_str: str) -> type:
    parts = type_str.split(".")

    for i in range(len(parts), 0, -1):
        module_name = ".".join(parts[:i])

        try:
            module = importlib.import_module(module_name)
        except ModuleNotFoundError as exception:
            if exception.name != module_name:
                raise
            continue

        result = module

        for part in parts[i:]:
            result = getattr(result, part)

        if not isinstance(result, type):
            raise TypeError(f"{type_str!r} does not identify a Python class")

        return result

    raise ImportError(f"Could not resolve Python type {type_str!r}")
