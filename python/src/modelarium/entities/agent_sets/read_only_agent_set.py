from __future__ import annotations

from collections.abc import Iterable, Iterator
from typing import Callable, TYPE_CHECKING

import jpype

from modelarium._java.wrapper import to_python, to_java
from modelarium._java.wrapper.protected_constructor_wrapper_object import ProtectedConstructorWrapperObject

if TYPE_CHECKING:
    from modelarium.entities.read_only.read_only_agent import ReadOnlyAgent
    from modelarium.utils.random_generator import RandomGenerator


class ReadOnlyAgentSet(Iterable[ReadOnlyAgent], ProtectedConstructorWrapperObject):
    def __iter__(self) -> Iterator[ReadOnlyAgent]:
        from modelarium.entities.read_only.read_only_agent import ReadOnlyAgent
        for java_agent in self._java_object:
            yield to_python(ReadOnlyAgent, java_agent)

    def get(self, agent_id: int | str) -> ReadOnlyAgent:
        from modelarium.entities.read_only.read_only_agent import ReadOnlyAgent
        return to_python(ReadOnlyAgent, self._java_object.get(agent_id))

    def get_as_list(self) -> list[ReadOnlyAgent]:
        from modelarium.entities.read_only.read_only_agent import ReadOnlyAgent
        java_list = self._java_object.getAsList()
        python_list = []
        for java_agent in java_list:
            python_list.append(to_python(ReadOnlyAgent, java_agent))
        return python_list

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
        from modelarium.entities.read_only.read_only_agent import ReadOnlyAgent

        java_agent_filter = jpype.JProxy(
            "java.util.function.Predicate",
            dict={"test": lambda java_agent: agent_filter(to_python(ReadOnlyAgent, java_agent))}
        )

        return to_python(ReadOnlyAgentSet, self._java_object.getFilteredAgents(java_agent_filter, include_dead_agents))

    def get_random_iterator(self, random_generator: RandomGenerator) -> Iterator[ReadOnlyAgent]:
        from modelarium.entities.read_only.read_only_agent import ReadOnlyAgent
        java_iterator = self._java_object.getRandomIterator(to_java(random_generator))
        for java_agent in java_iterator:
            yield to_python(ReadOnlyAgent, java_agent)
