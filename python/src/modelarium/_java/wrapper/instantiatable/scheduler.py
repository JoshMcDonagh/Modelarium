from __future__ import annotations

from typing import override, TYPE_CHECKING

from modelarium.scheduler.scheduler import Scheduler

if TYPE_CHECKING:
    from modelarium.entities.agent_sets.agent_set import AgentSet
    from modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment
    from modelarium.read_only_clock import ReadOnlyClock
    from modelarium.utils.random_generator import RandomGenerator


class _Scheduler(Scheduler):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.wrapper import WrapperObject
        WrapperObject.__init__(self, java_object)

    @override
    def run_tick(
            self,
            thread_name: str,
            clock: ReadOnlyClock,
            environment: ReadOnlyEnvironment,
            agent_set: AgentSet,
            random: RandomGenerator
    ) -> None:
        from modelarium._java.wrapper import to_java
        self._java_object.runTick(
            thread_name,
            to_java(clock),
            to_java(environment),
            to_java(agent_set),
            to_java(random)
        )



