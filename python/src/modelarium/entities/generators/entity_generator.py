from abc import ABC, abstractmethod
from collections.abc import Callable

import jpype

from python.src.modelarium.config import Config
from python.src.modelarium.entities.agent_sets.agent_set import AgentSet
from python.src.modelarium.entities.entity import Entity
from python.src.modelarium.utils.random_generator import RandomGenerator
from python.src.modelarium.wrapper_object import WrapperObject


class EntityGenerator(ABC, WrapperObject):
    def __init__(
            self,
            generator_function: Callable[[object, object], object],
            java_class_name: str,
            java_generate_method_to_call: str,
    ) -> None:
        generator_function = jpype.JProxy(
            "java.util.function.BiFunction",
            dict={java_generate_method_to_call: generator_function}
        )
        reset_function = jpype.JProxy("java.lang.Runnable", dict={"reset": self.reset})

        super().__init__(jpype.JClass(java_class_name)(generator_function, reset_function))

    @abstractmethod
    def reset(self) -> None:
        pass
