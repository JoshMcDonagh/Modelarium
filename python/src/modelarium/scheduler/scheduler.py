from abc import ABC, abstractmethod
from typing import override

import jpype

from modelarium.entities.agent_sets.agent_set import AgentSet, _AgentSet
from modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment
from modelarium.read_only_clock import ReadOnlyClock
from modelarium.utils.random_generator import RandomGenerator, _RandomGenerator
from modelarium.wrapper_object import WrapperObject


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


class _Scheduler(Scheduler):
    def __init__(self, java_object: object) -> None:
        super().__init__()
        self._java_obj = java_object

    @override
    def run_tick(
            self,
            thread_name: str,
            clock: ReadOnlyClock,
            environment: ReadOnlyEnvironment,
            agent_set: AgentSet,
            random: RandomGenerator
    ) -> None:
        self._java_object.runTick(
            thread_name,
            clock._java_object,
            environment._java_object,
            agent_set._java_object,
            random._java_object
        )
