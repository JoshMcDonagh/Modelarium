from __future__ import annotations

from collections.abc import Iterable, Iterator
from typing import Callable

import jpype

from modelarium._java.type_conversion import java_list_to_list
from modelarium._java.wrapper.protected_constructor_wrapper_object import ProtectedConstructorWrapperObject
from modelarium.entities.read_only.read_only_agent import ReadOnlyAgent
from modelarium.utils.random_generator import RandomGenerator


class ReadOnlyAgentSet(Iterable[ReadOnlyAgent], ProtectedConstructorWrapperObject):
    def __iter__(self) -> Iterator[ReadOnlyAgent]:
        for java_agent in self._java_object:
            yield ReadOnlyAgent(java_agent)

    def get(self, agent_id: int | str) -> ReadOnlyAgent:
        return ReadOnlyAgent(self._java_object.get(agent_id))

    def get_as_list(self) -> list[ReadOnlyAgent]:
        return java_list_to_list(self._java_object.getAsList(), ReadOnlyAgent)

    @property
    def size(self) -> int:
        return self._java_object.size()

    @property
    def is_empty(self) -> bool:
        return self._java_object.isEmpty()

    def does_agent_exist(self, agent_name: str) -> bool:
        return self._java_object.doesAgentExist(agent_name)

    def get_filtered_agents(
            self,
            agent_filter: Callable[[ReadOnlyAgent], bool],
            include_dead_agents: bool = False
    ) -> ReadOnlyAgentSet:
        java_agent_filter = jpype.JProxy(
            "java.util.function.Predicate",
            dict={"test": lambda java_agent: agent_filter(
                ReadOnlyAgent(java_agent)
            )}
        )

        return ReadOnlyAgentSet(self._java_object.getFilteredAgents(java_agent_filter, include_dead_agents))

    def get_random_iterator(self, random_generator: RandomGenerator) -> Iterator[ReadOnlyAgent]:
        java_iterator = self._java_object.getRandomIterator(random_generator._java_object)
        for java_agent in java_iterator:
            yield ReadOnlyAgent(java_agent)
