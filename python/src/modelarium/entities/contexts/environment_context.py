from __future__ import annotations

from typing import override, TYPE_CHECKING

from modelarium._java.wrapper import to_python
from modelarium.entities.contexts.context import Context

if TYPE_CHECKING:
    from modelarium.entities.environment import Environment
    from modelarium.entities.attributes.sets.environment_attribute_set import EnvironmentAttributeSet


class EnvironmentContext(Context):
    @override
    def get_this_entity(self) -> Environment:
        from modelarium.entities.environment import Environment
        return to_python(Environment, self._java_object.getThisEntity())

    @override
    def get_this_attribute_set(self) -> EnvironmentAttributeSet:
        from modelarium.entities.attributes.sets.environment_attribute_set import EnvironmentAttributeSet
        return to_python(EnvironmentAttributeSet, self._java_object.getThisAttributeSet())
