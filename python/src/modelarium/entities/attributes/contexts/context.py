from abc import ABC, abstractmethod

from python.src.modelarium.entities.agent import Agent
from python.src.modelarium.entities.attributes.attribute import Attribute
from python.src.modelarium.entities.entity import Entity
from python.src.modelarium.wrapper_object import WrapperObject


class Context(ABC, WrapperObject):
    def get_clock(self) -> object: # TODO: update to ReadOnlyClock type hint
        return self._java_object.getClock()

    def does_agent_exist_in_this_core(self, agent_name: str) -> bool:
        return self._java_object.doesAgentExistInThisCore(agent_name)

    def get_current_population_size(self) -> int:
        return self._java_object.getCurrentPopulationSize()

    def get_agent(self, target_agent_name: str) -> object: # TODO: update to ReadOnlyAgent type hint
        return self._java_object.getAgent(target_agent_name)

    def get_filtered_agents(self, filter: object, include_dead_agents: bool = False) -> object: # TODO update to Predicate<ReadOnlyAgent> (equivalent) and ReadOnlyAgentSet type hint
        return self._java_object.getFilteredAgents(filter, include_dead_agents)

    def get_random(self) -> object: # TODO: update to RandomGenerator type hint
        return self._java_object.getRandom()

    def add_agent(self, agent: Agent) -> None:
        return self._java_object.addAgent(agent._java_object)

    def add_agents(self, agents: object | list[Agent]) -> None: # TODO: update to AgentSet type hint
        return self._java_object.addAgents(agents)

    def kill_agent(self, agent: str | object) -> None: # TODO: update toe ReadOnlyAgent type hint
        return self._java_object.killAgent(agent)

    def kill_agents(self, agents: list[str] | object) -> None: # TODO: update to ReadOnlyAgentSet type hint
        return self._java_object.killAgents(agents)

    def get_this_attribute(self) -> Attribute:
        return Attribute(self._java_object.getThisAttribute())

    @abstractmethod
    def get_this_entity(self) -> Entity:
        pass

    @abstractmethod
    def get_this_attribute_set(self) -> object:  # TODO: Update with AttributeSet type hint
        pass
