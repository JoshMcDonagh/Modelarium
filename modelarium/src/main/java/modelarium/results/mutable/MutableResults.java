package modelarium.results.mutable;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.results.Results;
import modelarium.results.immutable.ImmutableResults;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for building up the results of a model run as it progresses.
 *
 * <p>This class is responsible for collecting the agent-level and environment-level results the model and its
 * workers produce, merging per-worker agent results together, and providing a read-only view of the results once
 * the run has completed.
 */
public final class MutableResults implements Results {

    /** The agent-level results of the model run */
    private MutableResultsForAgents agentsResults;

    /** The environment-level results of the model run */
    private MutableResultsForEnvironment environmentResults;

    /** The names of all agents in the model */
    private final List<String> agentNames = new ArrayList<>();

    /** Whether the agent results' underlying databases are currently connected */
    private boolean isAgentAttributeSetDataConnected = false;

    /** Whether the environment results' underlying databases are currently connected */
    private boolean isEnvironmentAttributeSetDataConnected = false;

    /** The immutable results version of this mutable results */
    private ImmutableResults immutableVersion = null;

    /**
     * Constructs a new, empty results container.
     */
    public MutableResults() {}

    /**
     * Stores the names of all agents in the model.
     *
     * @param agents the set of agents
     */
    public void setAgentNames(AgentSet agents) {
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

    /**
     * Returns the agent-level results of the model run.
     *
     * @return the run's {@link MutableResultsForAgents} instance
     */
    @Override
    public MutableResultsForAgents agents() {
        return agentsResults;
    }

    /**
     * Returns the environment-level results of the model run.
     *
     * @return the run's {@link MutableResultsForEnvironment} instance
     */
    @Override
    public MutableResultsForEnvironment environment() {
        return environmentResults;
    }

    /**
     * Exports the results of the model to a given export directory
     *
     * @param exportDir the directory to export the results to
     */
    @Override
    public void export(String exportDir) {
        String exportFolderName =
                "modelarium_results_export"
                + "_-_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_-_HH-mm-ss"));

        Path exportPath = Paths.get(exportDir, exportFolderName);

        if (Files.exists(exportPath))
            throw new IllegalStateException("Export path already exists: " + exportPath);

        try {
            Files.createDirectories(exportPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create path: " + exportPath, e);
        }

        environmentResults.export(exportPath);
        agentsResults.export(exportPath);
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
    public void mergeAgentsWith(MutableResults other) {
        agentsResults.mergeWith(other.agentsResults);
    }

    /**
     * Returns a read-only view of these results.
     *
     * @return a new {@link ImmutableResults} instance wrapping these results
     */
    public ImmutableResults getAsImmutable() {
        if (immutableVersion == null)
            immutableVersion = new ImmutableResults(this);

        return immutableVersion;
    }
}
