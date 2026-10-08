from abc import ABC, abstractmethod

from python.src.modelarium.entities.attributes.contexts.context import Context
from python.src.modelarium.entities.environment import Environment


class EnvironmentContext(ABC, Context):
    @abstractmethod
    def get_this_entity(self) -> Environment:
        pass

    @abstractmethod
    def get_this_attribute_set(self) -> object:  # TODO: Update with EnvironmentAttributeSet type hint
        pass
