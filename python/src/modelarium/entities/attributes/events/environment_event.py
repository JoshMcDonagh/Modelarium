from abc import ABC
from typing import override

from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
from modelarium.entities.attributes.events.event import Event
from modelarium.entities.contexts.environment_context import EnvironmentContext


class EnvironmentEvent(ABC, Event, EnvironmentAttribute):
    def __init__(self, name: str, is_logged: bool, access_level: AttributeAccessLevel) -> None:
        super().__init__(
            name,
            is_logged,
            access_level,
            "modelarium.entities.attributes.events.functional.FunctionalEnvironmentEvent",
            "modelarium.entities.attributes.events.functional.EnvironmentEventRunFunction",
            "modelarium.entities.attributes.events.functional.EnvironmentEventIsTriggeredFunction"
        )

    @override
    def _java_is_triggered(self, java_context: object) -> bool:
        context = EnvironmentContext(java_context)
        return self.is_triggered(context)

    @override
    def _java_run(self, java_context: object) -> None:
        context = EnvironmentContext(java_context)
        return self._run(context)


class _EnvironmentEvent(EnvironmentAttribute):
    def __init__(self, java_object: object) -> None:
        super().__init__(None, None, None)
        super()._java_obj = java_object
