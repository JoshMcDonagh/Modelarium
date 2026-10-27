from abc import ABC, abstractmethod

from modelarium._java import java_class
from modelarium._java.wrapper import to_java, to_python
from modelarium.config import Config
from modelarium.entities.agent_sets.agent_set import AgentSet
from modelarium.entities.generators.entity_generator import EntityGenerator
from modelarium.utils.random_generator import RandomGenerator


class AgentGenerator(ABC, EntityGenerator):
    def __init__(self) -> None:
        super().__init__(
            self._java_generate_agents_for_each_thread,
            "modelarium.entities.generators.FunctionalAgentGenerator",
            "generateAgentsForEachThread"
        )

    def _java_generate_agents_for_each_thread(self, java_config: object, java_random: object) -> object:
        config = to_python(Config, java_config)
        random = to_python(RandomGenerator, java_random)
        agent_sets = self.generate_agents_for_each_thread(config, random)
        java_agent_sets = java_class("java.util.ArrayList")()
        for agent_set in agent_sets:
            java_agent_sets.add(to_java(agent_set))
        return java_agent_sets

    @abstractmethod
    def generate_agents_for_each_thread(self, config: Config, random: RandomGenerator) -> list[AgentSet]:
        pass
