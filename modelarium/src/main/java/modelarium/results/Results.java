package modelarium.results;

import com.fasterxml.jackson.databind.ObjectMapper;
import modelarium.Config;
import modelarium.entities.Agent;
import modelarium.entities.agentsets.AgentSet;
import modelarium.internal.Internal;
import modelarium.results.readonly.ReadOnlyResults;

import java.io.File;
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
@Internal
public final class Results {
    private Config config = null;

    /** The agent-level results of the model run */
    private ResultsForAgents agentsResults;

    /** The environment-level results of the model run */
    private ResultsForEnvironment environmentResults;

    /** The names of all agents in the model */
    private final List<String> agentNames = new ArrayList<>();

    /** Whether the agent results' underlying databases are currently connected */
    private boolean isAgentAttributeSetDataConnected = false;

    /** Whether the environment results' underlying databases are currently connected */
    private boolean isEnvironmentAttributeSetDataConnected = false;

    /** The immutable results version of this mutable results */
    private ReadOnlyResults immutableVersion = null;

    /**
     * Constructs a new, empty results container.
     */
    public Results() {}

    /**
     * Sets the {@link Config} instance associated with the model.
     *
     * @param config the config instance to set
     */
    public void setConfig(Config config) {
        if (this.config != null)
            return;
        this.config = config;
    }

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
    public void setAgentResults(ResultsForAgents agentsResults) {
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
        this.environmentResults = environmentResults;
        if (environmentResults != null)
            isEnvironmentAttributeSetDataConnected = true;
    }

    /**
     * Returns the agent-level results of the model run.
     *
     * @return the run's {@link ResultsForAgents} instance
     */
    public ResultsForAgents agents() {
        return agentsResults;
    }

    /**
     * Returns the environment-level results of the model run.
     *
     * @return the run's {@link ResultsForEnvironment} instance
     */
    public ResultsForEnvironment environment() {
        return environmentResults;
    }

    private void exportConfig(Path exportedResultsPath) {
        if (config == null)
            throw new IllegalStateException("Config not set");

        String jsonFileName = "config.json";
        try {
            new ObjectMapper().writeValue(new File(exportedResultsPath.resolve(jsonFileName).toString()), config);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create '" + jsonFileName + "'", e);
        }
    }

    /**
     * Exports the results of the model to a given export directory
     *
     * @param exportDir the directory to export the results to
     * @return the {@link Path} directory containing the exported results
     */
    public Path export(String exportDir) {
        return export(Paths.get(exportDir).toAbsolutePath());
    }

    /**
     * Exports the results of the model to a given export directory given as a {@link Path}
     *
     * @param exportPath the path directory to export the results to
     * @return the {@link Path} directory containing the exported results
     */
    public Path export(Path exportPath) {
        String exportFolderName =
                "modelarium_results_export"
                        + "_-_"
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_-_HH-mm-ss"));

        Path exportedResultsPath = exportPath.resolve(exportFolderName);

        if (Files.exists(exportedResultsPath))
            throw new IllegalStateException("Export path already exists: " + exportedResultsPath);

        try {
            Files.createDirectories(exportedResultsPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create path: " + exportedResultsPath, e);
        }

        exportConfig(exportedResultsPath);
        environmentResults.export(exportedResultsPath);
        agentsResults.export(exportedResultsPath);

        return exportedResultsPath;
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
    public void mergeAgentsWith(Results other) {
        agentsResults.mergeWith(other.agentsResults);
    }

    /**
     * Returns a read-only view of these results.
     *
     * @return a new {@link ReadOnlyResults} instance wrapping these results
     */
    public ReadOnlyResults getAsImmutable() {
        if (immutableVersion == null)
            immutableVersion = new ReadOnlyResults(this);

        return immutableVersion;
    }
}
