package modelarium.entities.attributes.routines.functional;

import modelarium.entities.contexts.AgentContext;

@FunctionalInterface
public interface AgentRoutineRunFunction {
    void run(AgentContext context);
}
