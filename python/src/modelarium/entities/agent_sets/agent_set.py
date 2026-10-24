from __future__ import annotations

from collections.abc import Iterable, Iterator, Callable

import jpype

from modelarium._java import java_class
from modelarium.entities.agent import Agent, _Agent
from modelarium.entities.read_only.read_only_agent import ReadOnlyAgent
from modelarium._java.conversion import java_list_to_list, list_to_java_array_list
from modelarium.utils.random_generator import RandomGenerator
from modelarium.wrapper_object import WrapperObject


class AgentSet(Iterable[Agent], WrapperObject):
    def __init__(self, agents_list : list[Agent] | None = None) -> None:
        if agents_list is None:
            super().__init__(java_class("modelarium.entities.agentsets.AgentSet")())
        else:
            super().__init__(
                java_class("modelarium.entities.agentsets.AgentSet")(list_to_java_array_list(agents_list))
            )

    def __iter__(self) -> Iterator[Agent]:
        for java_agent in self._java_object:
            yield _Agent(java_agent)

    def add(self, agent: Agent | list[Agent] | AgentSet) -> None:
        if isinstance(agent, list):
            self._java_object.add(list_to_java_array_list(agent))
        else:
            self._java_object.add(agent._java_object)

    def add_deep_copy(self, agent: Agent | list[Agent] | AgentSet) -> None:
        if isinstance(agent, list):
            self._java_object.addDeepCopy(list_to_java_array_list(agent))
        else:
            self._java_object.addDeepCopy(agent._java_object)

    def get(self, agent_id: int | str) -> Agent:
        return _Agent(self._java_object.get(agent_id))

    def get_as_list(self) -> list[Agent]:
        return java_list_to_list(self._java_object.getAsList(), _Agent)

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
        self._java_object.update(other_agent_set._java_object, are_deep_copied)

    def get_filtered_agents(
            self,
            agent_filter: Callable[[ReadOnlyAgent], bool],
            include_dead_agents: bool = False
    ) -> AgentSet:
        java_agent_filter = jpype.JProxy(
            "java.util.function.Predicate",
            dict={"test": lambda java_agent: agent_filter(
                ReadOnlyAgent(java_agent)
            )}
        )

        return AgentSet(self._java_object.getFilteredAgents(java_agent_filter, include_dead_agents))

    def get_random_iterator(self, random_generator: RandomGenerator) -> Iterator[Agent]:
        java_iterator = self._java_object.getRandomIterator(random_generator._java_object)
        for java_agent in java_iterator:
            yield _Agent(java_agent)

    def get_as_immutable(self) -> ReadOnlyAgent:
        return ReadOnlyAgent(self._java_object.getAsImmutable())

    def duplicate(self) -> AgentSet:
        return _AgentSet(self._java_object.duplicate())


class _AgentSet(AgentSet):
    def __init__(self, java_object: object) -> None:
        WrapperObject.__init__(self, java_object)
