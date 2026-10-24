from typing import override

from modelarium.entities.attributes.sets.environment_attribute_set import EnvironmentAttributeSet, \
    _EnvironmentAttributeSet
from modelarium.entities.contexts.context import Context
from modelarium.entities.environment import Environment, _Environment


class EnvironmentContext(Context):
    @override
    def get_this_entity(self) -> Environment:
        return _Environment(self._java_object.getThisEntity())

    @override
    def get_this_attribute_set(self) -> EnvironmentAttributeSet:
        return _EnvironmentAttributeSet(self._java_object.getThisAttributeSet())
