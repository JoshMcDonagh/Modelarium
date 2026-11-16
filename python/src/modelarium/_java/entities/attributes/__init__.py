from modelarium.entities.attributes.agent_attribute import AgentAttribute


def java_agent_attribute_subclass_to_python(java_attribute: object) -> AgentAttribute:
    from modelarium._java import class_name_of

    if (class_name_of(java_attribute) ==
            "modelarium.entities.attributes.events.functional.FunctionalAgentEvent"):
        from modelarium._java import to_python
        from modelarium.entities.attributes.events.agent_event import AgentEvent
        return to_python(AgentEvent, java_attribute)

    if (class_name_of(java_attribute) ==
            "modelarium.python.entities.attributes.properties.functional.PythonFunctionalAgentProperty"):
        from modelarium._java import to_python
        from modelarium.entities.attributes.properties.agent_property import AgentProperty
        return to_python(AgentProperty, java_attribute)

    if (class_name_of(java_attribute) ==
            "modelarium.entities.attributes.routines.functional.FunctionalAgentRoutine"):
        from modelarium._java import to_python
        from modelarium.entities.attributes.routines.agent_routine import AgentRoutine
        return to_python(AgentRoutine, java_attribute)

    raise RuntimeError("Java attribute class type could not be converted to python object")
