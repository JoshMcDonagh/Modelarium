from typing import override

from modelarium._java.wrapper import to_python
from modelarium.entities.attributes.sets.environment_attribute_set import EnvironmentAttributeSet
from modelarium.entities.contexts.context import Context
from modelarium.entities.environment import Environment


class EnvironmentContext(Context):
    @override
    def get_this_entity(self) -> Environment:
        return to_python(Environment, self._java_object.getThisEntity())

    @override
    def get_this_attribute_set(self) -> EnvironmentAttributeSet:
        return to_python(EnvironmentAttributeSet, self._java_object.getThisAttributeSet())
