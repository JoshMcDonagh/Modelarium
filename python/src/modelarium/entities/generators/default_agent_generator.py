from abc import ABC, abstractmethod
from typing import override

from python.src.modelarium.config import Config
from python.src.modelarium.entities.agent import Agent
from python.src.modelarium.entities.agent_sets.agent_set import AgentSet
from python.src.modelarium.entities.generators.agent_generator import AgentGenerator
from python.src.modelarium.utils.random_generator import RandomGenerator


class DefaultAgentGenerator(ABC, AgentGenerator):
    def generate_agents(self, config: Config, random: RandomGenerator) -> AgentSet:
        agents = AgentSet()
        for _ in range(config.population_size):
            agents.add(self.generate_agent(config, random))
        return agents

    @override
    def generate_agents_for_each_thread(self, config: Config, random: RandomGenerator) -> list[AgentSet]:
        agents = self.generate_agents(config, random)
        num_of_threads = config.thread_count

        if num_of_threads < 1:
            return []

        if num_of_threads == 1:
            return [agents]

        agents_for_each_thread = []
        for _ in range(num_of_threads):
            agents_for_each_thread.append(AgentSet())

        thread = 0
        for agent in agents:
            agents_for_each_thread[thread].add(agent)
            thread = (thread + 1) % num_of_threads

        return agents_for_each_thread

    @abstractmethod
    def generate_agent(self, config: Config, random: RandomGenerator) -> Agent:
        pass