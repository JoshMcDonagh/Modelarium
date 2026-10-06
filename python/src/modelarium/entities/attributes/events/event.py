from abc import ABC

from typing_extensions import override

from python.src.modelarium.entities.attributes.attribute import Attribute
from python.src.modelarium.entities.attributes.events.read_only_event import ReadOnlyEvent


class Event(ABC, Attribute):
    def __str__(self) -> str:
        return self._java_object.toString()

    def is_triggered(self) -> bool:
        return self._java_object.isTriggered()

    @override
    def run(self) -> None:
        self._java_object.run()

    @override
    def get_as_immutable(self) -> ReadOnlyEvent:
        return ReadOnlyEvent(self._java_object.getAsImmutable())
