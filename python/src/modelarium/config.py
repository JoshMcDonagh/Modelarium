from __future__ import annotations

from dataclasses import dataclass
from datetime import timedelta
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from modelarium.entities.generators.agent_generator import AgentGenerator
    from modelarium.entities.generators.environment_generator import EnvironmentGenerator
    from modelarium.entities.logging.databases.factories.attribute_set_log_database_factory import \
        AttributeSetLogDatabaseFactory
    from modelarium.scheduler.scheduler import Scheduler


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
