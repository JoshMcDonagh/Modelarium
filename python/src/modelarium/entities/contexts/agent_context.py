from typing import override

from modelarium.entities.agent import Agent, _Agent
from modelarium.entities.attributes.sets.agent_attribute_set import AgentAttributeSet, _AgentAttributeSet
from modelarium.entities.contexts.context import Context
from modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment


class AgentContext(Context):
    @override
    def get_this_entity(self) -> Agent:
        return _Agent(self._java_object.getThisEntity())

    @override
    def get_this_attribute_set(self) -> AgentAttributeSet:
        return _AgentAttributeSet(self._java_object.getThisAttributeSet())

    @override
    def get_environment(self) -> ReadOnlyEnvironment:
        return ReadOnlyEnvironment(self._java_object.getEnvironment())
