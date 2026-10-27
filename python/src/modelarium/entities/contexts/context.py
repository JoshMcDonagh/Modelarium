from abc import ABC, abstractmethod
from typing import Callable

import jpype

from modelarium._java.wrapper import to_python, to_java
from modelarium._java.wrapper.protected_constructor_wrapper_object import ProtectedConstructorWrapperObject
from modelarium.entities.agent import Agent
from modelarium.entities.agent_sets.agent_set import AgentSet
from modelarium.entities.agent_sets.read_only_agent_set import ReadOnlyAgentSet
from modelarium.entities.attributes.attribute import Attribute
from modelarium.entities.attributes.sets.attribute_set import AttributeSet
from modelarium.entities.entity import Entity
from modelarium.entities.read_only.read_only_agent import ReadOnlyAgent
from modelarium.read_only_clock import ReadOnlyClock
from modelarium.utils.random_generator import RandomGenerator


class Context(ABC, ProtectedConstructorWrapperObject):
    def get_clock(self) -> ReadOnlyClock: #
        return to_python(ReadOnlyClock, self._java_object.getClock())

    def does_agent_exist_in_this_core(self, agent_name: str) -> bool:
        return self._java_object.doesAgentExistInThisCore(agent_name)

    def get_current_population_size(self) -> int:
        return self._java_object.getCurrentPopulationSize()

    def get_agent(self, target_agent_name: str) -> ReadOnlyAgent:
        return to_python(ReadOnlyAgent, self._java_object.getAgent(target_agent_name))

    def get_filtered_agents(
            self, filter: Callable[[ReadOnlyAgent], bool],
            include_dead_agents: bool = False
    ) -> ReadOnlyAgentSet:
        java_filter = jpype.JProxy(
            "java.util.function.Predicate",
            dict={"test": lambda java_agent: filter(ReadOnlyAgent(java_agent))}
        )
        return to_python(ReadOnlyAgentSet, self._java_object.getFilteredAgents(java_filter, include_dead_agents))

    def get_random(self) -> RandomGenerator:
        return to_python(RandomGenerator, self._java_object.getRandom())

    def add_agent(self, agent: Agent) -> None:
        return self._java_object.addAgent(to_java(agent))

    def add_agents(self, agents: AgentSet | list[Agent]) -> None:
        if isinstance(agents, AgentSet):
            return self._java_object.addAgents(to_java(agents))
        return self._java_object.addAgents(agents)

    def kill_agent(self, agent: str | ReadOnlyAgent) -> None:
        if isinstance(agent, ReadOnlyAgent):
            self._java_object.killAgent(to_java(agent))
        elif isinstance(agent, str):
            self._java_object.killAgent(agent)
        else:
            raise TypeError("Agent identifier must be a string or a ReadOnlyAgent, not: {}".format(type(agent)))

    def kill_agents(self, agents: list[str] | ReadOnlyAgentSet) -> None:
        if isinstance(agents, ReadOnlyAgentSet):
            return self._java_object.killAgents(to_java(agents))
        return self._java_object.killAgents(agents)

    def get_this_attribute(self) -> Attribute:
        return to_python(Attribute, self._java_object.getThisAttribute())

    @abstractmethod
    def get_this_entity(self) -> Entity:
        pass

    @abstractmethod
    def get_this_attribute_set(self) -> AttributeSet:
        pass
