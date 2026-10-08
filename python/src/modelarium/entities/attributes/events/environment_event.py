from abc import ABC

from python.src.modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from python.src.modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
from python.src.modelarium.entities.attributes.events.event import Event


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


class _EnvironmentEvent(EnvironmentAttribute):
    def __init__(self, java_object: object) -> None:
        super().__init__(None, None, None)
        super()._java_obj = java_object
