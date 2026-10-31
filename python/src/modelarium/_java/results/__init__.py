from typing import cast, Any


def java_log_value_to_python(value: object) -> object:
    from modelarium._java import java_class
    
    if value is None:
        return None

    PythonValue = java_class("modelarium.python.PythonValue")

    if isinstance(value, PythonValue):
        return cast(Any, value).payload().value

    return value
