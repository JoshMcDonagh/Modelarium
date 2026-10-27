from typing import override

from modelarium._java.wrapper import to_python
from modelarium.entities.agent import Agent
from modelarium.entities.attributes.sets.agent_attribute_set import AgentAttributeSet
from modelarium.entities.contexts.context import Context
from modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment


class AgentContext(Context):
    @override
    def get_this_entity(self) -> Agent:
        return to_python(Agent, self._java_object.getThisEntity())

    @override
    def get_this_attribute_set(self) -> AgentAttributeSet:
        return to_python(AgentAttributeSet, self._java_object.getThisAttributeSet())

    @override
    def get_environment(self) -> ReadOnlyEnvironment:
        return to_python(ReadOnlyEnvironment, self._java_object.getEnvironment())
