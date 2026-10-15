from abc import ABC
from typing import override

from python.src.modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
from python.src.modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from python.src.modelarium.entities.attributes.routines.routine import Routine
from python.src.modelarium.entities.contexts.environment_context import EnvironmentContext


class EnvironmentRoutine(ABC, Routine, EnvironmentAttribute):
    def __init__(self, name: str, access_level:AttributeAccessLevel) -> None:
        super().__init__(
            name,
            access_level,
            "modelarium.entities.attributes.routines.functional.FunctionalEnvironmentRoutine",
            "modelarium.entities.attributes.routines.functional.EnvironmentRoutineRunFunction"
        )

    @override
    def _java_run(self, java_context: object) -> None:
        context = EnvironmentContext(java_context)
        return self._run(context)


class _EnvironmentRoutine(EnvironmentRoutine):
    def __init__(self, java_object: object) -> None:
        super().__init__(None, None, None)
        super()._java_obj = java_object
