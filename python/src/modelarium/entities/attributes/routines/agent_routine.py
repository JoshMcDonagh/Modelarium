from abc import ABC

from python.src.modelarium.entities.attributes.agent_attribute import AgentAttribute
from python.src.modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from python.src.modelarium.entities.attributes.routines.routine import Routine


class AgentRoutine(ABC, Routine, AgentAttribute):
    def __init__(self, name: str, access_level:AttributeAccessLevel) -> None:
        super().__init__(
            name,
            access_level,
            "modelarium.entities.attributes.routines.functional.FunctionalAgentRoutine",
            "modelarium.entities.attributes.routines.functional.AgentRoutineRunFunction"
        )


class _AgentRoutine(AgentRoutine):
    def __init__(self, java_object: object) -> None:
        super().__init__(None, None, None)
        super()._java_obj = java_object
