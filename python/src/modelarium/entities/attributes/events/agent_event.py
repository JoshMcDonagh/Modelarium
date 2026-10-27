from abc import ABC
from typing import override

from modelarium.entities.attributes.agent_attribute import AgentAttribute
from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from modelarium.entities.attributes.events.event import Event
from modelarium.entities.contexts.agent_context import AgentContext


class AgentEvent(ABC, Event, AgentAttribute):
    def __init__(self, name: str, is_logged: bool, access_level: AttributeAccessLevel) -> None:
        super().__init__(
            name,
            is_logged,
            access_level,
            "modelarium.entities.attributes.events.functional.FunctionalAgentEvent",
            "modelarium.entities.attributes.events.functional.AgentEventRunFunction",
            "modelarium.entities.attributes.events.functional.AgentEventIsTriggeredFunction"
        )

    @override
    def _java_is_triggered(self, java_context: object) -> bool:
        context = AgentContext(java_context)
        return self._is_triggered(context)

    @override
    def _java_run(self, java_context: object) -> None:
        context = AgentContext(java_context)
        return self._run(context)



