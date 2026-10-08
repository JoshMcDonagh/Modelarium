from abc import ABC
from typing import override

from python.src.modelarium.entities.agent import Agent, _Agent
from python.src.modelarium.entities.attributes.contexts.context import Context
from python.src.modelarium.entities.attributes.read_only.read_only_environment import ReadOnlyEnvironment


class AgentContext(ABC, Context):
    @override
    def get_this_entity(self) -> Agent:
        return _Agent(self._java_object.getThisEntity())

    @override
    def get_this_attribute_set(self) -> object:  # TODO: Update with AgentAttributeSet type hint
        return self._java_object.getThisAttributeSet()

    @override
    def get_environment(self) -> ReadOnlyEnvironment:
        return ReadOnlyEnvironment(self._java_object.getEnvironment())
