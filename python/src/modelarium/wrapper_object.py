from abc import ABC

import jpype


class WrapperObject(ABC):
    def __init__(self, java_obj: jpype.JClass) -> None:
        self._java_obj = java_obj

    @property
    def _java_object(self) -> jpype.JClass:
        return self._java_obj
