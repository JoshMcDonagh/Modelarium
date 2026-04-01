package modelarium.results;

import modelarium.agents.Agent;
import modelarium.agents.sets.AgentSet;
import modelarium.logging.AttributeSetLog;
import modelarium.logging.databases.AttributeSetRunLogDatabase;
import modelarium.logging.databases.AttributeSetRunLogDatabaseFactory;

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

    private AgentLevelResults agentResults;
    private EnvironmentLevelResults environmentResults;

    private final Map<String, AttributeSetRunLogDatabase> accumulatedAgentAttributeSetResultsDatabaseMap = new HashMap<>();
    private final Map<String, AttributeSetRunLogDatabase> processedEnvironmentAttributeSetResultsDatabaseMap = new HashMap<>();
    private final List<AttributeSetRunLogDatabase> accumulatedAgentAttributeSetRunLogDatabaseList = new ArrayList<>();
    private final List<AttributeSetRunLogDatabase> processedEnvironmentAttributeSetRunLogDatabaseList = new ArrayList<>();

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
    public void setAgentResults(AgentLevelResults agentResults) {
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
    public void setEnvironmentResults(EnvironmentLevelResults environmentResults) {
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
            for (AttributeSetRunLogDatabase db : accumulatedAgentAttributeSetRunLogDatabaseList)
                db.disconnect();
            accumulatedAgentAttributeSetResultsDatabaseMap.clear();
            accumulatedAgentAttributeSetRunLogDatabaseList.clear();
            isAccumulatedAgentAttributeSetDataConnected = false;
        }
        if (isProcessedEnvironmentAttributeSetDataConnected) {
            for (AttributeSetRunLogDatabase db : processedEnvironmentAttributeSetRunLogDatabaseList)
                db.disconnect();
            processedEnvironmentAttributeSetResultsDatabaseMap.clear();
            processedEnvironmentAttributeSetRunLogDatabaseList.clear();
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

            if (accumulatedAgentAttributeSetRunLogDatabaseList.isEmpty()) {
                for (int j = 0; j < agentAttributeSetCollectionResults.getAttributeSetCount(); j++) {
                    String attributeName = agentAttributeSetCollectionResults.getAttributeSetResults(j).getAttributeSetName();
                    AttributeSetRunLogDatabase newDatabase = Objects.requireNonNull(AttributeSetRunLogDatabaseFactory.createDatabase(), "AttributeSetResultsDatabaseFactory.createDatabase() returned null");
                    newDatabase.connect();
                    accumulatedAgentAttributeSetResultsDatabaseMap.put(attributeName, newDatabase);
                    accumulatedAgentAttributeSetRunLogDatabaseList.add(newDatabase);
                }
                isAccumulatedAgentAttributeSetDataConnected = true;
            }

            for (int j = 0; j < agentAttributeSetCollectionResults.getAttributeSetCount(); j++) {
                AttributeSetLog agentAttributeSetLog = agentAttributeSetCollectionResults.getAttributeSetResults(j);
                String attributeName = agentAttributeSetLog.getAttributeSetName();

                List<String> propertyNamesList = agentAttributeSetLog.getPropertyNamesList();
                for (String propertyName : propertyNamesList) {
                    List<?> accumulatedValues = accumulatedAgentAttributeSetResultsDatabaseMap.get(attributeName).getPropertyColumnAsList(propertyName);
                    List<?> valuesToBeProcessed = agentAttributeSetLog.getPropertyValues(propertyName);
                    List<?> newAccumulatedValues = accumulateAgentPropertyResults(attributeName, propertyName, accumulatedValues, valuesToBeProcessed);
                    accumulatedAgentAttributeSetResultsDatabaseMap.get(attributeName).setPropertyColumn(propertyName, (List<Object>) newAccumulatedValues);
                }

                List<String> preEventNamesList = agentAttributeSetLog.getPreEventNamesList();
                for (String preEventName : preEventNamesList) {
                    List<?> accumulatedValues = accumulatedAgentAttributeSetResultsDatabaseMap.get(attributeName).getPreEventColumnAsList(preEventName);
                    List<Boolean> valuesToBeProcessed = agentAttributeSetLog.getPreEventValues(preEventName);
                    List<?> newAccumulatedValues = accumulateAgentPreEventResults(attributeName, preEventName, accumulatedValues, valuesToBeProcessed);
                    accumulatedAgentAttributeSetResultsDatabaseMap.get(attributeName).setPreEventColumn(preEventName, (List<Object>) newAccumulatedValues);
                }

                List<String> postEventNamesList = agentAttributeSetLog.getPostEventNamesList();
                for (String postEventName : postEventNamesList) {
                    List<?> accumulatedValues= accumulatedAgentAttributeSetResultsDatabaseMap.get(attributeName).getPostEventColumnAsList(postEventName);
                    List<Boolean> valuesToBeProcessed = agentAttributeSetLog.getPostEventValues(postEventName);
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

        if (processedEnvironmentAttributeSetRunLogDatabaseList.isEmpty()) {
            for (int i = 0; i < environmentAttributeSetCollectionResults.getAttributeSetCount(); i++) {
                String attributeName = environmentAttributeSetCollectionResults.getAttributeSetResults(i).getAttributeSetName();
                AttributeSetRunLogDatabase newDatabase = Objects.requireNonNull(AttributeSetRunLogDatabaseFactory.createDatabase(), "AttributeSetResultsDatabaseFactory.createDatabase() returned null");
                newDatabase.connect();
                processedEnvironmentAttributeSetResultsDatabaseMap.put(attributeName, newDatabase);
                processedEnvironmentAttributeSetRunLogDatabaseList.add(newDatabase);
            }
            isProcessedEnvironmentAttributeSetDataConnected = true;
        }

        for (int i = 0; i < environmentAttributeSetCollectionResults.getAttributeSetCount(); i++) {
            AttributeSetLog environmentAttributeSetLog = environmentAttributeSetCollectionResults.getAttributeSetResults(i);
            String attributeName = environmentAttributeSetLog.getAttributeSetName();

            List<String> propertyNamesList = environmentAttributeSetLog.getPropertyNamesList();
            for (String propertyName : propertyNamesList) {
                List<?> valuesToBeProcessed = environmentAttributeSetLog.getPropertyValues(propertyName);
                List<?> newProcessedValues = processEnvironmentPropertyResults(attributeName, propertyName, valuesToBeProcessed);
                processedEnvironmentAttributeSetResultsDatabaseMap.get(attributeName).setPropertyColumn(propertyName, (List<Object>) newProcessedValues);
            }

            List<String> preEventNamesList = environmentAttributeSetLog.getPreEventNamesList();
            for (String preEventName : preEventNamesList) {
                List<?> valuesToBeProcessed = environmentAttributeSetLog.getPreEventValues(preEventName);
                List<?> newProcessedValues = processEnvironmentPreEventResults(attributeName, preEventName, valuesToBeProcessed);
                processedEnvironmentAttributeSetResultsDatabaseMap.get(attributeName).setPreEventColumn(preEventName, (List<Object>) newProcessedValues);
            }

            List<String> postEventNamesList = environmentAttributeSetLog.getPostEventNamesList();
            for (String postEventName : postEventNamesList) {
                List<?> valuesToBeProcessed = environmentAttributeSetLog.getPostEventValues(postEventName);
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
