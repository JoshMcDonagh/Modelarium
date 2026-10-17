from abc import ABC, abstractmethod

import jpype

from python.src.modelarium.entities.agent_sets.agent_set import AgentSet, _AgentSet
from python.src.modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment
from python.src.modelarium.read_only_clock import ReadOnlyClock
from python.src.modelarium.utils.random_generator import RandomGenerator, _RandomGenerator
from python.src.modelarium.wrapper_object import WrapperObject


class Scheduler(ABC, WrapperObject):
    def __init__(self):
        tick_function = jpype.JProxy(
            "modelarium.scheduler.functional.TickFunction",
            dict={"runTick": self._java_run_tick}
        )

        super().__init__(jpype.JClass("modelarium.scheduler.functional.FunctionalScheduler")(tick_function))

    def _java_run_tick(
            self,
            thread_name: str,
            java_clock: object,
            java_environment: object,
            java_agent_set: object,
            java_random: object
    ):
        self.run_tick(
            thread_name,
            ReadOnlyClock(java_clock),
            ReadOnlyEnvironment(java_environment),
            _AgentSet(java_agent_set),
            _RandomGenerator(java_random)
        )

    @abstractmethod
    def run_tick(
            self,
            thread_name: str,
            clock: ReadOnlyClock,
            environment: ReadOnlyEnvironment,
            agent_set: AgentSet,
            random: RandomGenerator
    ) -> None:
        pass