package modelarium.results;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.AgentSet;

import java.util.*;

public class Results {
    private ResultsForAgents agentsResults;
    private ResultsForEnvironment environmentResults;

    private final List<String> agentNames = new ArrayList<>();

    private boolean isAgentAttributeSetDataConnected = false;
    private boolean isEnvironmentAttributeSetDataConnected = false;

    private boolean isImmutable = false;

    /**
     * Makes this Results instance immutable, preventing further modification.
     */
    public void seal() {
        isImmutable = true;
    }

    /**
     * Stores the names of all agents in the model.
     *
     * @param agents the set of agents
     */
    public void setAgentNames(AgentSet agents) {
        if (isImmutable)
            throw new IllegalStateException("Cannot modify Results: object is immutable.");

        for (Agent agent : agents)
            agentNames.add(agent.name());
    }

    /**
     * Stores the names of all agents from a list of agent sets.
     *
     * @param agentSetList list of agent sets
     */
    public void setAgentNames(List<AgentSet> agentSetList) {
        for (AgentSet agents : agentSetList)
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
    public void setAgentResults(ResultsForAgents agentsResults) {
        if (isImmutable)
            throw new IllegalStateException("Cannot modify Results: object is immutable.");

        this.agentsResults = agentsResults;
        if (agentsResults != null)
            isAgentAttributeSetDataConnected = true;
    }

    /**
     * Sets the raw environment results and connects the underlying database.
     *
     * @param environmentResults the raw environment results
     */
    public void setEnvironmentResults(ResultsForEnvironment environmentResults) {
        if (isImmutable)
            throw new IllegalStateException("Cannot modify Results: object is immutable.");

        this.environmentResults = environmentResults;
        if (environmentResults != null)
            isEnvironmentAttributeSetDataConnected = true;
    }

    public ResultsForAgents agents() {
        return agentsResults;
    }

    public ResultsForEnvironment environment() {
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
    public void mergeWith(Results other) {
        if (isImmutable)
            throw new IllegalStateException("Cannot modify Results: object is immutable.");
        agentsResults.mergeWith(other.agentsResults);
    }
}
