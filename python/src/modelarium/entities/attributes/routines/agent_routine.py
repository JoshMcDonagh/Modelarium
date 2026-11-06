from __future__ import annotations

from typing import override, TYPE_CHECKING

from modelarium.entities.attributes.agent_attribute import AgentAttribute
from modelarium.entities.attributes.routines.routine import Routine

if TYPE_CHECKING:
    from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel


class AgentRoutine(Routine, AgentAttribute):
    def __init__(self, name: str, access_level:AttributeAccessLevel) -> None:
        super().__init__(
            name,
            access_level,
            "modelarium.entities.attributes.routines.functional.FunctionalAgentRoutine",
            "modelarium.entities.attributes.routines.functional.AgentRoutineRunFunction"
        )

    @override
    def _java_run(self, java_context: object) -> None:
        from modelarium._java.wrapper import to_python
        from modelarium.entities.contexts.agent_context import AgentContext
        context = to_python(AgentContext, java_context)
        return self._run(context)
