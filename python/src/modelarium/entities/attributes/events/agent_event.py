from abc import ABC

from python.src.modelarium.entities.attributes.agent_attribute import AgentAttribute
from python.src.modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from python.src.modelarium.entities.attributes.events.event import Event


class AgentEvent(ABC, Event, AgentAttribute):
    def __init__(self, name: str, is_logged: bool, access_level: AttributeAccessLevel):
        super().__init__(
            name,
            is_logged,
            access_level,
            "modelarium.entities.attributes.events.functional.FunctionalAgentEvent",
            "modelarium.entities.attributes.events.functional.AgentEventRunFunction",
            "modelarium.entities.attributes.events.functional.AgentEventIsTriggeredFunction"
        )


class _AgentEvent(AgentEvent):
    def __init__(self, java_object: object) -> None:
        super().__init__(None, None, None)
        super()._java_obj = java_object
