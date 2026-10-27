from __future__ import annotations

from dataclasses import dataclass
from datetime import timedelta
from typing import Any

import jpype

from modelarium._java import java_class
from modelarium._java.type_conversion import timedelta_to_java_duration, java_duration_to_timedelta
from modelarium.entities.generators.agent_generator import AgentGenerator, _AgentGenerator
from modelarium.entities.generators.environment_generator import EnvironmentGenerator, _EnvironmentGenerator
from modelarium.entities.logging.databases.factories.attribute_set_log_database_factory import \
    AttributeSetLogDatabaseFactory, _AttributeSetLogDatabaseFactory
from modelarium.scheduler.scheduler import Scheduler, _Scheduler


def _config_to_java_config(config: Config) -> object:
    return java_class("modelarium.Config")(
        config.population_size,
        config.tick_count,
        config.thread_count,
        timedelta_to_java_duration(config.thread_timeout),
        config.are_threads_synced,
        config.agent_generator._java_object,
        config.environment_generator._java_object,
        config.scheduler._java_object,
        config.run_log_database_factory._java_object,
        jpype.JLong(config.seed)
    )


def _java_config_to_config(java_config: Any) -> Config:
    return Config(
        population_size=java_config.populationSize(),
        tick_count=java_config.tickCount(),
        thread_count=java_config.threadCount(),
        thread_timeout=java_duration_to_timedelta(java_config.threadTimeout()),
        are_threads_synced=java_config.areThreadsSynced(),
        agent_generator=_AgentGenerator(java_config.agentGenerator()),
        environment_generator=_EnvironmentGenerator(java_config.environmentGenerator()),
        scheduler=_Scheduler(java_config.scheduler()),
        run_log_database_factory=_AttributeSetLogDatabaseFactory(java_config.runLogDatabaseFactory()),
        seed=int(java_config.seed())
    )


@dataclass(frozen=True)
class Config:
    population_size: int
    tick_count: int
    thread_count: int
    thread_timeout: timedelta
    are_threads_synced: bool
    agent_generator: AgentGenerator
    environment_generator: EnvironmentGenerator
    scheduler: Scheduler
    run_log_database_factory: AttributeSetLogDatabaseFactory
    seed: int
