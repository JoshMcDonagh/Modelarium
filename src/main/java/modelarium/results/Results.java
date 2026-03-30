package modelarium.results;

import modelarium.agents.Agent;
import modelarium.agents.AgentSet;
import modelarium.attributes.results.AttributeSetCollectionResults;
import modelarium.attributes.results.AttributeSetResults;
import modelarium.attributes.results.databases.AttributeSetResultsDatabase;
import modelarium.attributes.results.databases.AttributeSetResultsDatabaseFactory;
import utils.DeepCopier;
import utils.DeepCopyable;

import java.util.*;

/**
 * Abstract base class for storing and manipulating simulation results.
 * <p>
 * Encapsulates agent and environment results, including raw data as well as processed or accumulated values.
 * The class supports transitioning to an immutable state after simulation and includes functionality
 * for connecting/disconnecting underlying result databases.
 * </p>
 * <p>
 * Subclasses must define how agent property and event data are accumulated.
 * </p>
 */
public abstract class Results implements DeepCopyable<Results> {

    private AgentResults agentResults;
    private EnvironmentResults environmentResults;

    private final Map<String, AttributeSetResultsDatabase> accumulatedAgentAttributeSetResultsDatabaseMap = new HashMap<>();
    private final Map<String, AttributeSetResultsDatabase> processedEnvironmentAttributeSetResultsDatabaseMap = new HashMap<>();
    private final List<AttributeSetResultsDatabase> accumulatedAgentAttributeSetResultsDatabaseList = new ArrayList<>();
    private final List<AttributeSetResultsDatabase> processedEnvironmentAttributeSetResultsDatabaseList = new ArrayList<>();

    private final List<String> agentNames = new ArrayList<>();

    private boolean isRawAgentAttributeSetDataConnected = false;
    private boolean isRawEnvironmentAttributeSetDataConnected = false;
    private boolean isAccumulatedAgentAttributeSetDataConnected = false;
    private boolean isProcessedEnvironmentAttributeSetDataConnected = false;

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
        return agentNames;
    }

    /**
     * Sets the raw agent results and connects the underlying database.
     *
     * @param agentResults the raw agent results
     */
    public void setAgentResults(AgentResults agentResults) {
        if (isImmutable)
            throw new IllegalStateException("Cannot modify Results: object is immutable.");

        this.agentResults = agentResults;
        if (agentResults != null)
            isRawAgentAttributeSetDataConnected = true;
    }

    /**
     * Sets the raw environment results and connects the underlying database.
     *
     * @param environmentResults the raw environment results
     */
    public void setEnvironmentResults(EnvironmentResults environmentResults) {
        if (isImmutable)
            throw new IllegalStateException("Cannot modify Results: object is immutable.");

        this.environmentResults = environmentResults;
        if (environmentResults != null)
            isRawEnvironmentAttributeSetDataConnected = true;
    }

    @Override
    public Results deepCopy() {
        return DeepCopier.deepCopy(this, this.getClass());
    }

    // === Raw Data Getters ===

    public List<Object> getAgentPropertyValues(String agentName, String attributeSetName, String propertyName) {
        if (!isRawAgentAttributeSetDataConnected)
            throw new IllegalStateException("Access of agent attribute database is not allowed when the appropriate database is disconnected.");
        return agentResults.getPropertyValues(agentName, attributeSetName, propertyName);
    }

    public List<Boolean> getAgentPreEventValues(String agentName, String attributeSetName, String eventName) {
        if (!isRawAgentAttributeSetDataConnected)
            throw new IllegalStateException("Access of agent attribute database is not allowed when the appropriate database is disconnected.");
        return agentResults.getPreEventValues(agentName, attributeSetName, eventName);
    }

    public List<Boolean> getAgentPostEventValues(String agentName, String attributeSetName, String eventName) {
        if (!isRawAgentAttributeSetDataConnected)
            throw new IllegalStateException("Access of agent attribute database is not allowed when the appropriate database is disconnected.");
        return agentResults.getPostEventValues(agentName, attributeSetName, eventName);
    }

    public List<Object> getEnvironmentPropertyValues(String attributeSetName, String propertyName) {
        if (!isRawEnvironmentAttributeSetDataConnected)
            throw new IllegalStateException("Access of environment attribute database is not allowed when the appropriate database is disconnected.");
        return environmentResults.getPropertyValues(attributeSetName, propertyName);
    }

    public List<Boolean> getEnvironmentPreEventValues(String attributeSetName, String eventName) {
        if (!isRawEnvironmentAttributeSetDataConnected)
            throw new IllegalStateException("Access of environment attribute database is not allowed when the appropriate database is disconnected.");
        return environmentResults.getPreEventValues(attributeSetName, eventName);
    }

    public List<Boolean> getEnvironmentPostEventValues(String attributeSetName, String eventName) {
        if (!isRawEnvironmentAttributeSetDataConnected)
            throw new IllegalStateException("Access of environment attribute database is not allowed when the appropriate database is disconnected.");
        return environmentResults.getPostEventValues(attributeSetName, eventName);
    }

    // === Accumulated Agent Data Getters ===

    public List<Object> getAccumulatedAgentPropertyValues(String attributeSetName, String propertyName) {
        if (!isAccumulatedAgentAttributeSetDataConnected)
            throw new IllegalStateException("Access of accumulated attribute database is not allowed when the appropriate database is disconnected.");
        return accumulatedAgentAttributeSetResultsDatabaseMap.get(attributeSetName).getPropertyColumnAsList(propertyName);
    }

    public List<Object> getAccumulatedAgentPreEventValues(String attributeSetName, String eventName) {
        if (!isAccumulatedAgentAttributeSetDataConnected)
            throw new IllegalStateException("Access of accumulated attribute database is not allowed when the appropriate database is disconnected.");
        return accumulatedAgentAttributeSetResultsDatabaseMap.get(attributeSetName).getPreEventColumnAsList(eventName);
    }

    public List<Object> getAccumulatedAgentPostEventValues(String attributeSetName, String eventName) {
        if (!isAccumulatedAgentAttributeSetDataConnected)
            throw new IllegalStateException("Access of accumulated attribute database is not allowed when the appropriate database is disconnected.");
        return accumulatedAgentAttributeSetResultsDatabaseMap.get(attributeSetName).getPostEventColumnAsList(eventName);
    }

    // === Accumulated Environment Data Getters ===

    public List<Object> getAccumulatedEnvironmentPropertyValues(String attributeSetName, String propertyName) {
        if (!isProcessedEnvironmentAttributeSetDataConnected)
            throw new IllegalStateException("Access of accumulated attribute database is not allowed when the appropriate database is disconnected.");
        return processedEnvironmentAttributeSetResultsDatabaseMap.get(attributeSetName).getPropertyColumnAsList(propertyName);
    }

    public List<Object> getAccumulatedEnvironmentPreEventValues(String attributeSetName, String eventName) {
        if (!isProcessedEnvironmentAttributeSetDataConnected)
            throw new IllegalStateException("Access of accumulated attribute database is not allowed when the appropriate database is disconnected.");
        return processedEnvironmentAttributeSetResultsDatabaseMap.get(attributeSetName).getPreEventColumnAsList(eventName);
    }

    public List<Object> getAccumulatedEnvironmentPostEventValues(String attributeSetName, String eventName) {
        if (!isProcessedEnvironmentAttributeSetDataConnected)
            throw new IllegalStateException("Access of accumulated attribute database is not allowed when the appropriate database is disconnected.");
        return processedEnvironmentAttributeSetResultsDatabaseMap.get(attributeSetName).getPostEventColumnAsList(eventName);
    }

    // === Database Disconnection ===

    /**
     * Disconnects all raw (per-agent and environment) databases if connected.
     */
    public void disconnectRawDatabases() {
        if (isRawAgentAttributeSetDataConnected) {
            agentResults.disconnectDatabases();
            isRawAgentAttributeSetDataConnected = false;
        }
        if (isRawEnvironmentAttributeSetDataConnected) {
            environmentResults.disconnectDatabases();
            isRawEnvironmentAttributeSetDataConnected = false;
        }
    }

    /**
     * Disconnects all accumulated/processed databases if connected.
     */
    public void disconnectAccumulatedDatabases() {
        if (isAccumulatedAgentAttributeSetDataConnected) {
            for (AttributeSetResultsDatabase db : accumulatedAgentAttributeSetResultsDatabaseList)
                db.disconnect();
            accumulatedAgentAttributeSetResultsDatabaseMap.clear();
            accumulatedAgentAttributeSetResultsDatabaseList.clear();
            isAccumulatedAgentAttributeSetDataConnected = false;
        }
        if (isProcessedEnvironmentAttributeSetDataConnected) {
            for (AttributeSetResultsDatabase db : processedEnvironmentAttributeSetResultsDatabaseList)
                db.disconnect();
            processedEnvironmentAttributeSetResultsDatabaseMap.clear();
            processedEnvironmentAttributeSetResultsDatabaseList.clear();
            isProcessedEnvironmentAttributeSetDataConnected = false;
        }
    }

    /**
     * Disconnects all raw and processed databases.
     */
    public void disconnectAllDatabases() {
        disconnectRawDatabases();
        disconnectAccumulatedDatabases();
    }

    // === Data Processing and Accumulation ===

    /**
     * Accumulates agent data over all ticks. Must be called before accessing accumulated values.
     */
    public void accumulateAgentAttributeData() {
        if (isImmutable)
            throw new IllegalStateException("Cannot modify Results: object is immutable.");

        for (int i = 0; i < agentResults.getAttributeSetCollectionSetCount(); i++) {
            AttributeSetCollectionResults agentAttributeSetCollectionResults = agentResults.getAttributeSetCollectionResults(i);

            if (accumulatedAgentAttributeSetResultsDatabaseList.isEmpty()) {
                for (int j = 0; j < agentAttributeSetCollectionResults.getAttributeSetCount(); j++) {
                    String attributeName = agentAttributeSetCollectionResults.getAttributeSetResults(j).getAttributeSetName();
                    AttributeSetResultsDatabase newDatabase = Objects.requireNonNull(AttributeSetResultsDatabaseFactory.createDatabase(), "AttributeSetResultsDatabaseFactory.createDatabase() returned null");
                    newDatabase.connect();
                    accumulatedAgentAttributeSetResultsDatabaseMap.put(attributeName, newDatabase);
                    accumulatedAgentAttributeSetResultsDatabaseList.add(newDatabase);
                }
                isAccumulatedAgentAttributeSetDataConnected = true;
            }

            for (int j = 0; j < agentAttributeSetCollectionResults.getAttributeSetCount(); j++) {
                AttributeSetResults agentAttributeSetResults = agentAttributeSetCollectionResults.getAttributeSetResults(j);
                String attributeName = agentAttributeSetResults.getAttributeSetName();

                List<String> propertyNamesList = agentAttributeSetResults.getPropertyNamesList();
                for (String propertyName : propertyNamesList) {
                    List<?> accumulatedValues = accumulatedAgentAttributeSetResultsDatabaseMap.get(attributeName).getPropertyColumnAsList(propertyName);
                    List<?> valuesToBeProcessed = agentAttributeSetResults.getPropertyValues(propertyName);
                    List<?> newAccumulatedValues = accumulateAgentPropertyResults(attributeName, propertyName, accumulatedValues, valuesToBeProcessed);
                    accumulatedAgentAttributeSetResultsDatabaseMap.get(attributeName).setPropertyColumn(propertyName, (List<Object>) newAccumulatedValues);
                }

                List<String> preEventNamesList = agentAttributeSetResults.getPreEventNamesList();
                for (String preEventName : preEventNamesList) {
                    List<?> accumulatedValues = accumulatedAgentAttributeSetResultsDatabaseMap.get(attributeName).getPreEventColumnAsList(preEventName);
                    List<Boolean> valuesToBeProcessed = agentAttributeSetResults.getPreEventValues(preEventName);
                    List<?> newAccumulatedValues = accumulateAgentPreEventResults(attributeName, preEventName, accumulatedValues, valuesToBeProcessed);
                    accumulatedAgentAttributeSetResultsDatabaseMap.get(attributeName).setPreEventColumn(preEventName, (List<Object>) newAccumulatedValues);
                }

                List<String> postEventNamesList = agentAttributeSetResults.getPostEventNamesList();
                for (String postEventName : postEventNamesList) {
                    List<?> accumulatedValues= accumulatedAgentAttributeSetResultsDatabaseMap.get(attributeName).getPostEventColumnAsList(postEventName);
                    List<Boolean> valuesToBeProcessed = agentAttributeSetResults.getPostEventValues(postEventName);
                    List<?> newAccumulatedValues= accumulateAgentPostEventResults(attributeName, postEventName, accumulatedValues, valuesToBeProcessed);
                    accumulatedAgentAttributeSetResultsDatabaseMap.get(attributeName).setPostEventColumn(postEventName, (List<Object>) newAccumulatedValues);
                }
            }
        }
    }

    /**
     * Processes the environment data across all ticks. Must be called before accessing processed environment values.
     */
    public void processEnvironmentAttributeData() {
        if (isImmutable)
            throw new IllegalStateException("Cannot modify Results: object is immutable.");

        AttributeSetCollectionResults environmentAttributeSetCollectionResults = environmentResults.getAttributeSetCollectionResults();

        if (processedEnvironmentAttributeSetResultsDatabaseList.isEmpty()) {
            for (int i = 0; i < environmentAttributeSetCollectionResults.getAttributeSetCount(); i++) {
                String attributeName = environmentAttributeSetCollectionResults.getAttributeSetResults(i).getAttributeSetName();
                AttributeSetResultsDatabase newDatabase = Objects.requireNonNull(AttributeSetResultsDatabaseFactory.createDatabase(), "AttributeSetResultsDatabaseFactory.createDatabase() returned null");
                newDatabase.connect();
                processedEnvironmentAttributeSetResultsDatabaseMap.put(attributeName, newDatabase);
                processedEnvironmentAttributeSetResultsDatabaseList.add(newDatabase);
            }
            isProcessedEnvironmentAttributeSetDataConnected = true;
        }

        for (int i = 0; i < environmentAttributeSetCollectionResults.getAttributeSetCount(); i++) {
            AttributeSetResults environmentAttributeSetResults = environmentAttributeSetCollectionResults.getAttributeSetResults(i);
            String attributeName = environmentAttributeSetResults.getAttributeSetName();

            List<String> propertyNamesList = environmentAttributeSetResults.getPropertyNamesList();
            for (String propertyName : propertyNamesList) {
                List<?> valuesToBeProcessed = environmentAttributeSetResults.getPropertyValues(propertyName);
                List<?> newProcessedValues = processEnvironmentPropertyResults(attributeName, propertyName, valuesToBeProcessed);
                processedEnvironmentAttributeSetResultsDatabaseMap.get(attributeName).setPropertyColumn(propertyName, (List<Object>) newProcessedValues);
            }

            List<String> preEventNamesList = environmentAttributeSetResults.getPreEventNamesList();
            for (String preEventName : preEventNamesList) {
                List<?> valuesToBeProcessed = environmentAttributeSetResults.getPreEventValues(preEventName);
                List<?> newProcessedValues = processEnvironmentPreEventResults(attributeName, preEventName, valuesToBeProcessed);
                processedEnvironmentAttributeSetResultsDatabaseMap.get(attributeName).setPreEventColumn(preEventName, (List<Object>) newProcessedValues);
            }

            List<String> postEventNamesList = environmentAttributeSetResults.getPostEventNamesList();
            for (String postEventName : postEventNamesList) {
                List<?> valuesToBeProcessed = environmentAttributeSetResults.getPostEventValues(postEventName);
                List<?> newProcessedValues = processEnvironmentPostEventResults(attributeName, postEventName, valuesToBeProcessed);
                processedEnvironmentAttributeSetResultsDatabaseMap.get(attributeName).setPostEventColumn(postEventName, (List<Object>) newProcessedValues);
            }
        }
    }

    /**
     * Merges agent results from another simulation run prior to accumulation.
     *
     * @param otherResults the results to merge into this one
     */
    public void mergeWithBeforeAccumulation(Results otherResults) {
        if (isImmutable)
            throw new IllegalStateException("Cannot modify Results: object is immutable.");
        agentResults.mergeWith(otherResults.agentResults);
    }

    // === Abstract Methods ===

    /**
     * Subclasses must define how to accumulate agent property values.
     */
    protected abstract List<?> accumulateAgentPropertyResults(String attributeSetName, String propertyName,
                                                              List<?> accumulatedValues, List<?> valuesToBeProcessed);

    /**
     * Subclasses must define how to accumulate agent pre-event values.
     */
    protected abstract List<?> accumulateAgentPreEventResults(String attributeSetName, String preEventName,
                                                              List<?> accumulatedValues, List<Boolean> valuesToBeProcessed);

    /**
     * Subclasses must define how to accumulate agent post-event values.
     */
    protected abstract List<?> accumulateAgentPostEventResults(String attributeSetName, String postEventName,
                                                               List<?> accumulatedValues, List<Boolean> valuesToBeProcessed);

    // === Optional Overridable Hooks for Environment Post-Processing ===

    protected List<?> processEnvironmentPropertyResults(String attributeName, String propertyName, List<?> propertyValues) {
        return propertyValues;
    }

    protected List<?> processEnvironmentPreEventResults(String attributeName, String eventName, List<?> preEventValues) {
        return preEventValues;
    }

    protected List<?> processEnvironmentPostEventResults(String attributeName, String eventName, List<?> postEventValues) {
        return postEventValues;
    }
}
