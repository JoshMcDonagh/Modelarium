import copy
from typing import cast, Any

import jpype

from modelarium._java import java_class
from modelarium.entities.attributes.properties import PROPERTY_T


class PythonPayload:
    def __init__(self, value: object) -> None:
        self.value = value


def make_payload_proxy(value: object) -> object:
    return jpype.JProxy("modelarium.python.PythonObject", inst=PythonPayload(value), convert=True)


def copy_payload(payload: PythonPayload) -> object:
    return make_payload_proxy(copy.deepcopy(payload.value))


_copier_proxy = None


def get_copier_proxy() -> object:
    global _copier_proxy
    if _copier_proxy is None:
        _copier_proxy = jpype.JProxy("modelarium.python.PythonValueCopier", dict={"copy": copy_payload})
    return _copier_proxy


def string_of_payload(payload: PythonPayload) -> str:
    return str(payload.value)


_stringifier_proxy = None


def get_stringifier_proxy() -> object:
    global _stringifier_proxy
    if _stringifier_proxy is None:
        _stringifier_proxy = jpype.JProxy(
            "modelarium.python.PythonValueStringifier",
            dict={"stringOf": string_of_payload}
        )
    return _stringifier_proxy


def python_to_java(
    value_type: type[PROPERTY_T],
    value: PROPERTY_T
) -> object:
    if value is None:
        return None

    if not isinstance(value, value_type):
        raise TypeError(f"Expected {value_type.__name__}, got {type(value).__name__}")

    return java_class("modelarium.python.PythonValue")(
        make_payload_proxy(value),
        get_copier_proxy(),
        get_stringifier_proxy()
    )


def java_to_python(
    value_type: type[PROPERTY_T],
    java_value: object
) -> PROPERTY_T | None:
    if java_value is None:
        return None

    java_PythonValue = java_class("modelarium.python.PythonValue")

    if not isinstance(java_value, java_PythonValue):
        raise TypeError("Expected a Java PythonValue")

    payload = cast(Any, java_value).payload()

    if not isinstance(payload, PythonPayload):
        raise TypeError("Invalid PythonValue payload")

    value = payload.value

    if not isinstance(value, value_type):
        raise TypeError(f"Expected {value_type.__name__}, got {type(value).__name__}")

    return value
