from typing import Any


class WrapperObject:
    def __init__(self, java_obj) -> None:
        self._java_obj = java_obj

    @property
    def _java_object(self) -> Any:
        return self._java_obj
