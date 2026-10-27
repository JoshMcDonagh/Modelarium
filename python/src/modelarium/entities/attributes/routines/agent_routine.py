from abc import ABC
from typing import override

from modelarium._java.wrapper import to_python
from modelarium.entities.attributes.agent_attribute import AgentAttribute
from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from modelarium.entities.attributes.routines.routine import Routine
from modelarium.entities.contexts.agent_context import AgentContext


class AgentRoutine(ABC, Routine, AgentAttribute):
    def __init__(self, name: str, access_level:AttributeAccessLevel) -> None:
        super().__init__(
            name,
            access_level,
            "modelarium.entities.attributes.routines.functional.FunctionalAgentRoutine",
            "modelarium.entities.attributes.routines.functional.AgentRoutineRunFunction"
        )

    @override
    def _java_run(self, java_context: object) -> None:
        context = to_python(AgentContext, java_context)
        return self._run(context)
