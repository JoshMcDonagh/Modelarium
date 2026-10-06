from typing import override

from python.src.modelarium.entities.attributes.read_only_attribute import ReadOnlyAttribute


class ReadOnlyEvent(ReadOnlyAttribute):
    @override
    def __str__(self) -> str:
        return self._java_object.toString()

    def is_triggered(self) -> bool:
        return self._java_object.isTriggered()
