from typing import Any

from modelarium._java.convertible_python_object import ConvertiblePythonObject


class WrapperObject(ConvertiblePythonObject):
    def __init__(self, java_obj) -> None:
        self._java_obj = java_obj

    def __str__(self) -> str:
        return self._java_object.toString()

    @property
    def _java_object(self) -> Any:
        return self._java_obj

    @property
    def _java_class_name(self) -> str:
        return str(self._java_obj.class_.getName())
