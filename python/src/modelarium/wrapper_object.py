from abc import ABC


class WrapperObject(ABC):
    def __init__(self, java_obj: object) -> None:
        self._java_obj = java_obj

    @property
    def _java_object(self) -> object:
        return self._java_obj
