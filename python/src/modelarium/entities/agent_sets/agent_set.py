from __future__ import annotations

from collections.abc import Iterable, Iterator, Callable
from typing import TYPE_CHECKING

import jpype

from modelarium._java import java_class
from modelarium._java.type_conversion import list_to_java_array_list
from modelarium._java.wrapper import to_python, to_java
from modelarium._java.wrapper.wrapper_object import WrapperObject

if TYPE_CHECKING:
    from modelarium.entities.agent import Agent
    from modelarium.entities.agent_sets.read_only_agent_set import ReadOnlyAgentSet
    from modelarium.entities.read_only.read_only_agent import ReadOnlyAgent
    from modelarium.utils.random_generator import RandomGenerator


class AgentSet(Iterable[Agent], WrapperObject):
    def __init__(self, agents_list: list[Agent] | None = None) -> None:
        if agents_list is None:
            WrapperObject.__init__(self, java_class("modelarium.entities.agentsets.AgentSet")())
        else:
            WrapperObject.__init__(
                self, java_class("modelarium.entities.agentsets.AgentSet")(list_to_java_array_list(agents_list))
            )

    def __iter__(self) -> Iterator[Agent]:
        from modelarium.entities.agent import Agent
        for java_agent in self._java_object:
            yield to_python(Agent, java_agent)

    def add(self, agent: Agent | list[Agent] | AgentSet) -> None:
        if isinstance(agent, list):
            self._java_object.add(list_to_java_array_list(agent))
        else:
            self._java_object.add(to_java(agent))

    def add_deep_copy(self, agent: Agent | list[Agent] | AgentSet) -> None:
        if isinstance(agent, list):
            self._java_object.addDeepCopy(list_to_java_array_list(agent))
        else:
            self._java_object.addDeepCopy(to_java(agent))

    def get(self, agent_id: int | str) -> Agent:
        from modelarium.entities.agent import Agent
        return to_python(Agent, self._java_object.get(agent_id))

    def get_as_list(self) -> list[Agent]:
        from modelarium.entities.agent import Agent
        java_list = self._java_object.getAsList()
        python_list = []
        for java_agent in java_list:
            python_list.append(to_python(Agent, java_agent))
        return python_list

    @property
    def size(self) -> int:
        return self._java_object.size()

    @property
    def is_empty(self) -> bool:
        return self._java_object.isEmpty()

    def clear(self) -> None:
        return self._java_object.clear()

    def does_agent_exist(self, agent_name: str) -> bool:
        return self._java_object.doesAgentExist(agent_name)

    def update(self, other_agent_set: AgentSet, are_deep_copied: bool) -> None:
        self._java_object.update(to_java(other_agent_set), are_deep_copied)

    def get_filtered_agents(
            self,
            agent_filter: Callable[[ReadOnlyAgent], bool],
            include_dead_agents: bool = False
    ) -> AgentSet:
        from modelarium.entities.read_only.read_only_agent import ReadOnlyAgent

        java_agent_filter = jpype.JProxy(
            "java.util.function.Predicate",
            dict={"test": lambda java_agent: agent_filter(to_python(ReadOnlyAgent, java_agent))}
        )

        return to_python(AgentSet, self._java_object.getFilteredAgents(java_agent_filter, include_dead_agents))

    def get_random_iterator(self, random_generator: RandomGenerator) -> Iterator[Agent]:
        from modelarium.entities.agent import Agent
        java_iterator = self._java_object.getRandomIterator(to_java(random_generator))
        for java_agent in java_iterator:
            yield to_python(Agent, java_agent)

    def get_as_immutable(self) -> ReadOnlyAgentSet:
        from modelarium.entities.agent_sets.read_only_agent_set import ReadOnlyAgentSet
        return to_python(ReadOnlyAgentSet, self._java_object.getAsImmutable())

    def duplicate(self) -> AgentSet:
        return to_python(AgentSet, self._java_object.duplicate())
