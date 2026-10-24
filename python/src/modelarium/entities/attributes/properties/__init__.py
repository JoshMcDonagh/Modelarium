import copy
from typing import TypeVar, cast, Any

import jpype

from modelarium._java import java_class

PROPERTY_T = TypeVar("PROPERTY_T")


class _PythonPayload:
    def __init__(self, value: object) -> None:
        self.value = value


def _make_payload_proxy(value: object) -> object:
    return jpype.JProxy("modelarium.python.PythonObject", inst=_PythonPayload(value), convert=True)


def _copy_payload(payload: _PythonPayload) -> object:
    return _make_payload_proxy(copy.deepcopy(payload.value))


_copier_proxy = None


def _get_copier_proxy() -> object:
    global _copier_proxy
    if _copier_proxy is None:
        _copier_proxy = jpype.JProxy("modelarium.python.PythonValueCopier", dict={"copy": _copy_payload})
    return _copier_proxy


def _python_to_java(
    value_type: type[PROPERTY_T],
    value: PROPERTY_T
) -> object:
    if value is None:
        return None

    if not isinstance(value, value_type):
        raise TypeError(f"Expected {value_type.__name__}, got {type(value).__name__}")

    return java_class("modelarium.python.PythonValue")(_make_payload_proxy(value), _get_copier_proxy())


def _java_to_python(
    value_type: type[PROPERTY_T],
    java_value: object
) -> PROPERTY_T | None:
    if java_value is None:
        return None

    java_PythonValue = java_class("modelarium.python.PythonValue")

    if not isinstance(java_value, java_PythonValue):
        raise TypeError("Expected a Java PythonValue")

    payload = cast(Any, java_value).payload()

    if not isinstance(payload, _PythonPayload):
        raise TypeError("Invalid PythonValue payload")

    value = payload.value

    if not isinstance(value, value_type):
        raise TypeError(f"Expected {value_type.__name__}, got {type(value).__name__}")

    return value
