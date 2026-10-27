from typing import TypeVar, Any, cast

import jpype

from modelarium._java import java_class
from modelarium._java.type_conversion import java_duration_to_timedelta, timedelta_to_java_duration
from modelarium._java.wrapper.instantiatable import _ReadOnlyClock
from modelarium._java.wrapper.instantiatable.entities import _Agent, _Environment, _AgentSet, _ReadOnlyAgentSet, \
    _AgentEvent, _EnvironmentEvent, _ReadOnlyEvent, _AgentProperty, _EnvironmentProperty, _ReadOnlyProperty, \
    _AgentRoutine, _EnvironmentRoutine, _ReadOnlyRoutine, _ReadOnlyAgentAttributeSet, _ReadOnlyEnvironmentAttributeSet, \
    _AgentContext, _EnvironmentContext, _AgentGenerator, _DefaultAgentGenerator, _EnvironmentGenerator, \
    _AttributeSetLog, _EntityLog, _AttributeSetLogDatabase, _DiskBasedAttributeSetLogDatabase, \
    _MemoryBasedAttributeSetLogDatabase, _AttributeSetLogDatabaseFactory, _ReadOnlyAgent, _ReadOnlyEnvironment, \
    _Attribute, _Entity, _Context, _ReadOnlyAttribute, _Event, _Property, _Routine, _AttributeSet, _ReadOnlyEntity, \
    _ReadOnlyAttributeSet, _AgentAttribute, _EnvironmentAttribute, _AgentAttributeSet, _EnvironmentAttributeSet
from modelarium._java.wrapper.instantiatable.results import _ReadOnlyResults, _ReadOnlyResultsForAgents
from modelarium._java.wrapper.wrapper_object import WrapperObject
from modelarium.config import Config
from modelarium.entities.agent import Agent
from modelarium.entities.agent_sets.agent_set import AgentSet
from modelarium.entities.agent_sets.read_only_agent_set import ReadOnlyAgentSet
from modelarium.entities.attributes.agent_attribute import AgentAttribute
from modelarium.entities.attributes.attribute import Attribute
from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
from modelarium.entities.attributes.events.agent_event import AgentEvent
from modelarium.entities.attributes.events.environment_event import EnvironmentEvent
from modelarium.entities.attributes.events.event import Event
from modelarium.entities.attributes.events.read_only_event import ReadOnlyEvent
from modelarium.entities.attributes.properties.agent_property import AgentProperty
from modelarium.entities.attributes.properties.environment_property import EnvironmentProperty
from modelarium.entities.attributes.properties.property import Property
from modelarium.entities.attributes.properties.read_only_property import ReadOnlyProperty
from modelarium.entities.attributes.read_only_attribute import ReadOnlyAttribute
from modelarium.entities.attributes.routines.agent_routine import AgentRoutine
from modelarium.entities.attributes.routines.environment_routine import EnvironmentRoutine
from modelarium.entities.attributes.routines.read_only_routine import ReadOnlyRoutine
from modelarium.entities.attributes.routines.routine import Routine
from modelarium.entities.attributes.sets.agent_attribute_set import AgentAttributeSet
from modelarium.entities.attributes.sets.attribute_set import AttributeSet
from modelarium.entities.attributes.sets.environment_attribute_set import EnvironmentAttributeSet
from modelarium.entities.attributes.sets.read_only.read_only_agent_attribute_set import ReadOnlyAgentAttributeSet
from modelarium.entities.attributes.sets.read_only.read_only_attribute_set import ReadOnlyAttributeSet
from modelarium.entities.attributes.sets.read_only.read_only_environment_attribute_set import \
    ReadOnlyEnvironmentAttributeSet
from modelarium.entities.contexts.agent_context import AgentContext
from modelarium.entities.contexts.context import Context
from modelarium.entities.contexts.environment_context import EnvironmentContext
from modelarium.entities.entity import Entity
from modelarium.entities.environment import Environment
from modelarium.entities.generators.agent_generator import AgentGenerator
from modelarium.entities.generators.default_agent_generator import DefaultAgentGenerator
from modelarium.entities.generators.environment_generator import EnvironmentGenerator
from modelarium.entities.logging.attribute_set_log import AttributeSetLog
from modelarium.entities.logging.databases.attribute_set_log_database import AttributeSetLogDatabase
from modelarium.entities.logging.databases.disk_based_attribute_set_log_database import DiskBasedAttributeSetLogDatabase
from modelarium.entities.logging.databases.factories.attribute_set_log_database_factory import \
    AttributeSetLogDatabaseFactory
from modelarium.entities.logging.databases.memory_based_attribute_set_log_database import \
    MemoryBasedAttributeSetLogDatabase
from modelarium.entities.logging.entity_log import EntityLog
from modelarium.entities.read_only.read_only_agent import ReadOnlyAgent
from modelarium.entities.read_only.read_only_entity import ReadOnlyEntity
from modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment
from modelarium.read_only_clock import ReadOnlyClock
from modelarium.results.read_only.read_only_results import ReadOnlyResults
from modelarium.results.read_only.read_only_results_for_agents import ReadOnlyResultsForAgents
from modelarium.results.read_only.read_only_results_for_environment import ReadOnlyResultsForEnvironment
from modelarium.scheduler.scheduler import Scheduler

WRAPPER_OBJECT_T = TypeVar("WRAPPER_OBJECT_T", bound=WrapperObject | Config)


_INSTANTIATABLE_WRAPPER_OBJECT_TYPES_DICT: dict[type[WrapperObject], type[WrapperObject]] = {
    ReadOnlyClock: _ReadOnlyClock,

    # Entities
    Entity: _Entity,
    Agent: _Agent,
    Environment: _Environment,
    AgentSet: _AgentSet,
    ReadOnlyAgentSet: _ReadOnlyAgentSet,
    Attribute: _Attribute,
    ReadOnlyAttribute: _ReadOnlyAttribute,
    AgentAttribute: _AgentAttribute,
    EnvironmentAttribute: _EnvironmentAttribute,
    Event: _Event,
    AgentEvent: _AgentEvent,
    EnvironmentEvent: _EnvironmentEvent,
    ReadOnlyEvent: _ReadOnlyEvent,
    Property: _Property,
    AgentProperty: _AgentProperty,
    EnvironmentProperty: _EnvironmentProperty,
    ReadOnlyProperty: _ReadOnlyProperty,
    Routine: _Routine,
    AgentRoutine: _AgentRoutine,
    EnvironmentRoutine: _EnvironmentRoutine,
    ReadOnlyRoutine: _ReadOnlyRoutine,
    AttributeSet: _AttributeSet,
    AgentAttributeSet: _AgentAttributeSet,
    EnvironmentAttributeSet: _EnvironmentAttributeSet,
    ReadOnlyAttributeSet: _ReadOnlyAttributeSet,
    ReadOnlyAgentAttributeSet: _ReadOnlyAgentAttributeSet,
    ReadOnlyEnvironmentAttributeSet: _ReadOnlyEnvironmentAttributeSet,
    Context: _Context,
    AgentContext: _AgentContext,
    EnvironmentContext: _EnvironmentContext,
    AgentGenerator: _AgentGenerator,
    DefaultAgentGenerator: _DefaultAgentGenerator,
    EnvironmentGenerator: _EnvironmentGenerator,
    AttributeSetLog: _AttributeSetLog,
    EntityLog: _EntityLog,
    AttributeSetLogDatabase: _AttributeSetLogDatabase,
    DiskBasedAttributeSetLogDatabase: _DiskBasedAttributeSetLogDatabase,
    MemoryBasedAttributeSetLogDatabase: _MemoryBasedAttributeSetLogDatabase,
    AttributeSetLogDatabaseFactory: _AttributeSetLogDatabaseFactory,
    ReadOnlyEntity: _ReadOnlyEntity,
    ReadOnlyAgent: _ReadOnlyAgent,
    ReadOnlyEnvironment: _ReadOnlyEnvironment,

    # Results
    ReadOnlyResults: _ReadOnlyResults,
    ReadOnlyResultsForAgents: _ReadOnlyResultsForAgents,
    ReadOnlyResultsForEnvironment: _ReadOnlyResultsForAgents
}


def _config_to_python(java_config: Any) -> Config:
    return Config(
        population_size=java_config.populationSize(),
        tick_count=java_config.tickCount(),
        thread_count=java_config.threadCount(),
        thread_timeout=java_duration_to_timedelta(java_config.threadTimeout()),
        are_threads_synced=java_config.areThreadsSynced(),
        agent_generator=_AgentGenerator(java_config.agentGenerator()),
        environment_generator=_EnvironmentGenerator(java_config.environmentGenerator()),
        scheduler=to_python(Scheduler, java_config.scheduler()),
        run_log_database_factory=_AttributeSetLogDatabaseFactory(java_config.runLogDatabaseFactory()),
        seed=int(java_config.seed())
    )


def _attribute_access_level_to_python(java_version: object) -> AttributeAccessLevel:
    java_enum = java_class("modelarium.entities.attributes.AttributeAccessLevel")

    if java_version == java_enum.PUBLIC:
        return cast(AttributeAccessLevel, cast(object, AttributeAccessLevel.PUBLIC))
    if java_version == java_enum.PRIVATE:
        return cast(AttributeAccessLevel, cast(object, AttributeAccessLevel.PRIVATE))

    raise ValueError(f"Java attribute access level {java_version!r} is not supported")


def to_python(wrapper_type: type[WRAPPER_OBJECT_T], obj: object) -> WRAPPER_OBJECT_T:
    if isinstance(wrapper_type, type) and wrapper_type == Config:
        return _config_to_python(obj)
    if isinstance(wrapper_type, type) and wrapper_type == AttributeAccessLevel:
        return _attribute_access_level_to_python(obj)
    return _INSTANTIATABLE_WRAPPER_OBJECT_TYPES_DICT[cast(type[WrapperObject], wrapper_type)](obj)


def _config_to_java(config: Config) -> object:
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


def to_java(obj: WRAPPER_OBJECT_T) -> object:
    if isinstance(obj, Config):
        return _config_to_java(obj)
    return obj._java_object
