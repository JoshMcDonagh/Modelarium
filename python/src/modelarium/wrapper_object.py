from abc import ABC
from typing import Any


class WrapperObject(ABC):
    def __init__(self, java_obj: object) -> None:
        self._java_obj = java_obj

    @property
    def _java_object(self) -> Any:
        return self._java_obj
