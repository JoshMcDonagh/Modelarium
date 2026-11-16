from typing import cast, Any

from modelarium._java.language_conversion import PYTHON_OBJECT_T


def value_to_python(python_type: type[PYTHON_OBJECT_T], java_value: object) -> PYTHON_OBJECT_T:
    from modelarium._java import java_class
    java_PythonValue_class = java_class("modelarium.python.PythonValue")

    if not isinstance(java_value, java_PythonValue_class):
        raise TypeError(f"Expected a Java PythonValue")

    payload = cast(Any, java_value).payload()

    from modelarium._java.language_conversion.values import PythonPayload
    if not isinstance(payload, PythonPayload):
        raise TypeError("Invalid PythonValue payload")

    python_value = payload.value

    if not isinstance(python_value, python_type):
        raise TypeError(f"Expected {python_type.__name__}, got {type(python_value).__name__}")

    return python_value
