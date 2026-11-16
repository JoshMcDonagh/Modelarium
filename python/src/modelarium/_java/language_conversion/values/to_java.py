from __future__ import annotations

import copy
from typing import TYPE_CHECKING

import jpype

if TYPE_CHECKING:
    from modelarium._java.language_conversion.values import PythonPayload


def _make_payload_proxy(value: object) -> object:
    from modelarium._java.language_conversion.values import PythonPayload
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


def value_to_java(python_value: object) -> object:
    from modelarium._java import java_class
    return java_class("modelarium.python.PythonValue")(
        _make_payload_proxy(python_value),
        _get_copier_proxy(),
        _get_stringifier_proxy()
    )
