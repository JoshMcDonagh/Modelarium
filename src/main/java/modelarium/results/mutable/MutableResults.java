package modelarium.results.mutable;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.MutableAgentSet;
import modelarium.results.immutable.ImmutableResults;

import java.util.ArrayList;
import java.util.List;

public class MutableResults {
    private MutableResultsForAgents agentsResults;
    private MutableResultsForEnvironment environmentResults;

    private final List<String> agentNames = new ArrayList<>();

    private boolean isAgentAttributeSetDataConnected = false;
    private boolean isEnvironmentAttributeSetDataConnected = false;

    public MutableResults() {}

    protected MutableResults(MutableResults other) {
        this.agentsResults = other.agentsResults;
        this.environmentResults = other.environmentResults;
        this.agentNames.addAll(other.agentNames);
        this.isAgentAttributeSetDataConnected = other.isAgentAttributeSetDataConnected;
        this.isEnvironmentAttributeSetDataConnected = other.isEnvironmentAttributeSetDataConnected;
    }

    /**
     * Stores the names of all agents in the model.
     *
     * @param agents the set of agents
     */
    public void setAgentNames(MutableAgentSet agents) {
        for (Agent agent : agents)
            agentNames.add(agent.name());
    }

    /**
     * Stores the names of all agents from a list of agent sets.
     *
     * @param agentSetList list of agent sets
     */
    public void setAgentNames(List<MutableAgentSet> agentSetList) {
        for (MutableAgentSet agents : agentSetList)
            setAgentNames(agents);
    }

    /**
     * Returns a list of all agent names involved in the model.
     *
     * @return the list of agent names
     */
    public List<String> getAgentNames() {
        return new ArrayList<>(agentNames);
    }

    /**
     * Sets the raw agent results and connects the underlying database.
     *
     * @param agentsResults the raw agent results
     */
    public void setAgentResults(MutableResultsForAgents agentsResults) {
        this.agentsResults = agentsResults;
        if (agentsResults != null)
            isAgentAttributeSetDataConnected = true;
    }

    /**
     * Sets the raw environment results and connects the underlying database.
     *
     * @param environmentResults the raw environment results
     */
    public void setEnvironmentResults(MutableResultsForEnvironment environmentResults) {
        this.environmentResults = environmentResults;
        if (environmentResults != null)
            isEnvironmentAttributeSetDataConnected = true;
    }

    public MutableResultsForAgents agents() {
        return agentsResults;
    }

    public MutableResultsForEnvironment environment() {
        return environmentResults;
    }

    /**
     * Disconnects all raw (per-agent and environment) databases if connected.
     */
    public void disconnectDatabases() {
        if (isAgentAttributeSetDataConnected) {
            agentsResults.disconnectDatabases();
            isAgentAttributeSetDataConnected = false;
        }
        if (isEnvironmentAttributeSetDataConnected) {
            environmentResults.disconnectDatabases();
            isEnvironmentAttributeSetDataConnected = false;
        }
    }

    /**
     * Merges agent results from another simulation run prior to accumulation.
     *
     * @param other the results to merge into this one
     */
    public void mergeWith(MutableResults other) {
        agentsResults.mergeWith(other.agentsResults);
    }

    public ImmutableResults getAsImmutable() {
        return new ImmutableResults(this);
    }
}
