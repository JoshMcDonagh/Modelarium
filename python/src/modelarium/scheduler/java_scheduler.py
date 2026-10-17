from abc import ABC
from typing import override

from python.src.modelarium.entities.agent_sets.agent_set import AgentSet
from python.src.modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment
from python.src.modelarium.read_only_clock import ReadOnlyClock
from python.src.modelarium.scheduler.scheduler import Scheduler
from python.src.modelarium.utils.random_generator import RandomGenerator


class JavaScheduler(ABC, Scheduler):
    def __init__(self, java_obj: object):
        super().__init__()
        super()._java_obj = java_obj

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
