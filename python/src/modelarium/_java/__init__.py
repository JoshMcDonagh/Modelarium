from typing import Any, cast

import jpype

from modelarium._java.language_conversion import PYTHON_OBJECT_T


def java_class(java_class_name: str) -> Any:
    return cast(Any, jpype.JClass(java_class_name))


def java_class_name_of(java_object: object) -> str:
    return str(cast(Any, java_object).class_.getName())


def java_parent_class_names_of(java_object: object) -> list[str]:
    java_parent_class = cast(Any, java_object).class_.getSuperclass()
    parent_class_names = []
    while java_parent_class is not None:
        parent_class_names.append(str(java_parent_class.getName()))
        java_parent_class = java_parent_class.getSuperclass()
    return parent_class_names


def to_python(python_type: type[PYTHON_OBJECT_T], java_object: object) -> PYTHON_OBJECT_T:
    if java_object is None:
        return None

    from modelarium._java.language_conversion.subclasses import subclass_needs_to_be_converted_from
    if subclass_needs_to_be_converted_from(python_type):
        from modelarium._java.language_conversion.subclasses.to_python import to_python_subclass_type
        return to_python_subclass_type(python_type, java_object)

    from modelarium._java.objects.convertible_python_object import ConvertiblePythonObject
    if issubclass(python_type, ConvertiblePythonObject):
        from modelarium._java.language_conversion.instantiatable_wrappers import java_to_python_wrapper
        return java_to_python_wrapper(python_type, java_object)

    from modelarium._java.language_conversion.types import is_defined_python_type
    if is_defined_python_type(python_type):
        from modelarium._java.language_conversion.types.to_python import to_defined_python_type
        return to_defined_python_type(python_type, java_object)

    from modelarium._java.language_conversion.values.to_python import value_to_python
    return value_to_python(python_type, java_object)


def to_java(python_object: PYTHON_OBJECT_T) -> object:
    if python_object is None:
        return None

    from modelarium._java.objects.convertible_python_object import ConvertiblePythonObject
    if isinstance(python_object, ConvertiblePythonObject):
        from modelarium._java.language_conversion.instantiatable_wrappers import python_wrapper_to_java
        return python_wrapper_to_java(python_object)

    from modelarium._java.language_conversion.types import is_instance_of_defined_python_type
    if is_instance_of_defined_python_type(python_object):
        from modelarium._java.language_conversion.types.to_java import to_defined_java_type
        return to_defined_java_type(python_object)

    from modelarium._java.language_conversion.values.to_java import value_to_java
    return value_to_java(python_object)


def create_python_value_codec() -> object:
    from modelarium._java.python_value_codec import encode
    from modelarium._java.python_value_codec import decode
    return jpype.JProxy("modelarium.python.PythonValueCodec", dict = {"encode": encode, "decode": decode})
