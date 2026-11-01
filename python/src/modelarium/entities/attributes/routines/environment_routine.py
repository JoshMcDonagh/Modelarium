from __future__ import annotations

from abc import ABC
from typing import override, TYPE_CHECKING

from modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
from modelarium.entities.attributes.routines.routine import Routine

if TYPE_CHECKING:
    from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel


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
        from modelarium._java.wrapper import to_python
        from modelarium.entities.contexts.environment_context import EnvironmentContext
        context = to_python(EnvironmentContext, java_context)
        return self._run(context)
