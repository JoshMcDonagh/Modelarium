from abc import ABC, abstractmethod

from python.src.modelarium.entities.attributes.contexts.context import Context
from python.src.modelarium.entities.environment import Environment, _Environment


class EnvironmentContext(ABC, Context):
    @abstractmethod
    def get_this_entity(self) -> Environment:
        return _Environment(self._java_object.getThisEntity())

    @abstractmethod
    def get_this_attribute_set(self) -> object:  # TODO: Update with EnvironmentAttributeSet type hint
        return self._java_object.getThisAttributeSet()
