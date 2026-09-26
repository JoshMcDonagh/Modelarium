from abc import ABC

import jpype


class WrapperObject(ABC):
    def __init__(self, java_object: jpype.JClass):
        self._java_object = java_object

    @property
    def java_object(self):
        return self._java_object