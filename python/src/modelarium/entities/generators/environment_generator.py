from abc import ABC, abstractmethod

from modelarium._java.wrapper import to_python, to_java
from modelarium.config import Config
from modelarium.entities.environment import Environment
from modelarium.entities.generators.entity_generator import EntityGenerator
from modelarium.utils.random_generator import RandomGenerator


class EnvironmentGenerator(ABC, EntityGenerator):
    def __init__(self) -> None:
        super().__init__(
            self._java_generate_environment,
            "modelarium.entities.generators.FunctionalEnvironmentGenerator",
            "generateEnvironment"
        )

    def _java_generate_environment(self, java_config: object, java_random: object) -> object:
        config = to_python(Config, java_config)
        random = to_python(RandomGenerator, java_random)
        return to_java(self.generate_environment(config, random))

    @abstractmethod
    def generate_environment(self, config: Config, random: RandomGenerator) -> Environment:
        pass
