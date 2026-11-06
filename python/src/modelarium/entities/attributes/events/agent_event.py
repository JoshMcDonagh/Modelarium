from __future__ import annotations

from typing import override, TYPE_CHECKING

from modelarium.entities.attributes.agent_attribute import AgentAttribute
from modelarium.entities.attributes.events.event import Event

if TYPE_CHECKING:
    from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel


class AgentEvent(Event, AgentAttribute):
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
        from modelarium._java.wrapper import to_python
        from modelarium.entities.contexts.agent_context import AgentContext
        context = to_python(AgentContext, java_context)
        return self._is_triggered(context)

    @override
    def _java_run(self, java_context: object) -> None:
        from modelarium._java.wrapper import to_python
        from modelarium.entities.contexts.agent_context import AgentContext
        context = to_python(AgentContext, java_context)
        return self._run(context)
