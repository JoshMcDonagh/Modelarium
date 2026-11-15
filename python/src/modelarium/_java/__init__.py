import copy
from typing import Any, cast, TypeVar

import jpype


def java_class(class_name: str) -> Any:
    return cast(Any, jpype.JClass(class_name))


PYTHON_OBJECT_T = TypeVar("PYTHON_OBJECT_T")


class PythonPayload:
    def __init__(self, value: object) -> None:
        self.value = value


def to_python(python_type: type[PYTHON_OBJECT_T], java_object: object) -> PYTHON_OBJECT_T:
    if java_object is None:
        return None

    from modelarium._java.convertible_python_object import ConvertiblePythonObject
    if issubclass(python_type, ConvertiblePythonObject):
        from modelarium._java.wrapper import java_to_python_wrapper
        return java_to_python_wrapper(python_type, java_object)

    java_PythonValue = java_class("modelarium.python.PythonValue")

    if not isinstance(java_object, java_PythonValue):
        raise TypeError(f"Expected a Java PythonValue")

    payload = cast(Any, java_object).payload()

    if not isinstance(payload, PythonPayload):
        raise TypeError("Invalid PythonValue payload")

    python_value = payload.value

    if not isinstance(python_value, python_type):
        raise TypeError(f"Expected {python_type.__name__}, got {type(python_value).__name__}")

    return python_value


def _make_payload_proxy(value: object) -> object:
    return jpype.JProxy("modelarium.python.PythonObject", inst=PythonPayload(value), convert=True)


_copier_proxy = None


def _copy_payload(payload: PythonPayload) -> object:
    return _make_payload_proxy(copy.deepcopy(payload.value))


def _get_copier_proxy() -> object:
    global _copier_proxy
    if _copier_proxy is None:
        _copier_proxy = jpype.JProxy("modelarium.python.PythonValueCopier", dict={"copy": _copy_payload})
    return _copier_proxy


_stringifier_proxy = None


def _string_of_payload(payload: PythonPayload) -> str:
    return str(payload.value)


def _get_stringifier_proxy() -> object:
    global _stringifier_proxy
    if _stringifier_proxy is None:
        _stringifier_proxy = jpype.JProxy(
            "modelarium.python.PythonValueStringifier",
            dict={"stringOf": _string_of_payload}
        )
    return _stringifier_proxy


def to_java(python_object: PYTHON_OBJECT_T) -> object:
    if python_object is None:
        return None

    from modelarium._java.convertible_python_object import ConvertiblePythonObject
    if isinstance(python_object, ConvertiblePythonObject):
        from modelarium._java.wrapper import python_wrapper_to_java
        return python_wrapper_to_java(python_object)

    return java_class("modelarium.python.PythonValue")(
        _make_payload_proxy(python_object),
        _get_copier_proxy(),
        _get_stringifier_proxy()
    )