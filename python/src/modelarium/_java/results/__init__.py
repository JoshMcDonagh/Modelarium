from typing import cast, Any

import jpype


def java_log_value_to_python(value: object) -> object:
    if value is None:
        return None

    PythonValue = jpype.JClass("modelarium.python.PythonValue")

    if isinstance(value, PythonValue):
        return cast(Any, value).payload().value

    return value
