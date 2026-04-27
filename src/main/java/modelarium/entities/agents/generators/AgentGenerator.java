package modelarium.entities.agents.generators;

import modelarium.Config;
import modelarium.entities.agents.AgentSet;

import java.util.List;

public interface AgentGenerator {
    AgentSet generateAgents(Config config);
    List<AgentSet> getAgentsForEachCore(Config config);
}
