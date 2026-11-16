from typing import Any, cast

import jpype

from modelarium._java.language_conversion import PYTHON_OBJECT_T


def java_class(java_class_name: str) -> Any:
    return cast(Any, jpype.JClass(java_class_name))


def class_name_of(java_object: object) -> str:
    return str(cast(Any, java_object).class_.getName())


def to_python(python_type: type[PYTHON_OBJECT_T], java_object: object) -> PYTHON_OBJECT_T:
    if java_object is None:
        return None

    from modelarium._java.convertible_python_object import ConvertiblePythonObject
    if issubclass(python_type, ConvertiblePythonObject):
        from modelarium._java.wrapper import java_to_python_wrapper
        return java_to_python_wrapper(python_type, java_object)

    from modelarium._java.language_conversion.values.to_python import value_to_python
    return value_to_python(python_type, java_object)


def to_java(python_object: PYTHON_OBJECT_T) -> object:
    if python_object is None:
        return None

    from modelarium._java.convertible_python_object import ConvertiblePythonObject
    if isinstance(python_object, ConvertiblePythonObject):
        from modelarium._java.wrapper import python_wrapper_to_java
        return python_wrapper_to_java(python_object)

    from modelarium._java.language_conversion.values.to_java import value_to_java
    return value_to_java(python_object)
