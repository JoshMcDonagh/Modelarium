from __future__ import annotations

from typing import override, TYPE_CHECKING

from modelarium._java.wrapper import to_python
from modelarium.entities.contexts.context import Context

if TYPE_CHECKING:
    from modelarium.entities.agent import Agent
    from modelarium.entities.attributes.agent_attribute import AgentAttribute
    from modelarium.entities.attributes.sets.agent_attribute_set import AgentAttributeSet
    from modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment


class AgentContext(Context):
    @override
    def get_this_attribute(self) -> AgentAttribute:
        from modelarium.entities.attributes.agent_attribute import AgentAttribute
        return to_python(AgentAttribute, self._java_object.getThisAttribute())

    @override
    def get_this_entity(self) -> Agent:
        from modelarium.entities.agent import Agent
        return to_python(Agent, self._java_object.getThisEntity())

    @override
    def get_this_attribute_set(self) -> AgentAttributeSet:
        from modelarium.entities.attributes.sets.agent_attribute_set import AgentAttributeSet
        return to_python(AgentAttributeSet, self._java_object.getThisAttributeSet())

    @override
    def get_environment(self) -> ReadOnlyEnvironment:
        from modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment
        return to_python(ReadOnlyEnvironment, self._java_object.getEnvironment())
