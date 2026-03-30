package modelarium;

import modelarium.agents.AgentGenerator;
import modelarium.agents.DefaultAgentGenerator;
import modelarium.environments.DefaultEnvironmentGenerator;
import modelarium.environments.EnvironmentGenerator;
import modelarium.results.Results;
import modelarium.scheduler.ModelScheduler;
import com.google.gson.reflect.TypeToken;
import utils.DeepCopier;

import java.lang.reflect.InvocationTargetException;

/**
 * Encapsulates all configurable settings for a simulation model run.
 *
 * <p>Provides setters for various parameters such as the number of agents,
 * simulation length, multithreading behaviour, result handling,
 * and environment/agent generators.
 */
public class ModelSettings {

    // Core simulation parameters
    private int numOfAgents = 1;
    private int numOfCores = 1;
    private int numOfTicksToRun = 1;
    private int numOfWarmUpTicks = 0;

    // Attribute configurations for agents and the environment
    private AttributeSetCollection baseAgentAttributeSetCollection = new AttributeSetCollection();
    private AttributeSetCollection baseEnvironmentAttributeSetCollection = new AttributeSetCollection();

    // Generators for agents and the environment
    private AgentGenerator agentGenerator = new DefaultAgentGenerator();
    private EnvironmentGenerator environmentGenerator = new DefaultEnvironmentGenerator();

    // Flags for process coordination and optimisation
    private boolean areProcessesSynced = false;
    private boolean doAgentStoresHoldAgentCopies = false;
    private boolean isCacheUsed = false;

    // Attribute configurations for attribute set results storage

    private boolean areAttributeSetResultsStoredOnDisk = true;

    // Core components required for simulation
    private Class<? extends Results> resultsClass = null;
    private Results results = null;
    private ModelScheduler modelScheduler;

    // === Setters ===

    /** Sets the number of agents to initialise. */
    public void setNumOfAgents(int numOfAgents) {
        this.numOfAgents = numOfAgents;
    }

    /** Sets the number of processing cores to use for simulation. */
    public void setNumOfCores(int numOfCores) {
        this.numOfCores = numOfCores;
    }

    /** Sets the number of simulation ticks (excluding warm-up). */
    public void setNumOfTicksToRun(int numOfTicksToRun) {
        this.numOfTicksToRun = numOfTicksToRun;
    }

    /** Sets the number of warm-up ticks before data is collected. */
    public void setNumOfWarmUpTicks(int numOfWarmUpTicks) {
        this.numOfWarmUpTicks = numOfWarmUpTicks;
    }

    /** Sets the base attribute configuration for all agents. */
    public void setBaseAgentAttributeSetCollection(AttributeSetCollection baseAgentAttributeSetCollection) {
        this.baseAgentAttributeSetCollection = baseAgentAttributeSetCollection;
    }

    /** Sets the base attribute configuration for the environment. */
    public void setBaseEnvironmentAttributeSetCollection(AttributeSetCollection baseEnvironmentAttributeSetCollection) {
        this.baseEnvironmentAttributeSetCollection = baseEnvironmentAttributeSetCollection;
    }

    /** Specifies whether simulation processes are synchronised (e.g., central coordinator). */
    public void setAreProcessesSynced(boolean areProcessesSynced) {
        this.areProcessesSynced = areProcessesSynced;
    }

    /** Sets whether agent stores retain copies of agents or references. */
    public void setDoAgentStoresHoldAgentCopies(boolean doAgentStoresHoldAgentCopies) {
        this.doAgentStoresHoldAgentCopies = doAgentStoresHoldAgentCopies;
    }

    /** Enables or disables use of caching mechanisms within agent stores. */
    public void setIsCacheUsed(boolean isCacheUsed) {
        this.isCacheUsed = isCacheUsed;
    }

    /** Sets whether attribute set results are stored on disk or in memory. */
    public void setAreAttributeSetResultsStoredOnDisk(boolean areAttributeSetResultsStoredOnDisk) {
        this.areAttributeSetResultsStoredOnDisk = areAttributeSetResultsStoredOnDisk;
    }

    /** Sets the results class that will be used to store and process simulation data. */
    public <T extends Results> void setResultsClass(Class<T> resultsClass) {
        this.resultsClass = resultsClass;
    }

    /** Sets the results instance that will be used to store and process simulation data. */
    public void setResults(Results results) {
        this.results = results;
    }

    /** Sets the generator responsible for creating initial agent populations. */
    public void setAgentGenerator(AgentGenerator agentGenerator) {
        this.agentGenerator = agentGenerator;
    }

    /** Sets the generator responsible for creating the simulation environment. */
    public void setEnvironmentGenerator(EnvironmentGenerator environmentGenerator) {
        this.environmentGenerator = environmentGenerator;
    }

    /** Sets the scheduler used to coordinate agent and environment updates. */
    public void setModelScheduler(ModelScheduler modelScheduler) {
        this.modelScheduler = modelScheduler;
    }

    // === Getters ===

    /** @return the number of agents configured for the model */
    public int getNumOfAgents() {
        return numOfAgents;
    }

    /** @return the number of cores the simulation will use */
    public int getNumOfCores() {
        return numOfCores;
    }

    /** @return the number of active simulation ticks (excluding warm-up) */
    public int getNumOfTicksToRun() {
        return numOfTicksToRun;
    }

    /** @return the number of warm-up ticks before simulation begins recording data */
    public int getNumOfWarmUpTicks() {
        return numOfWarmUpTicks;
    }

    /** @return the total number of ticks including warm-up and active simulation */
    public int getTotalNumOfTicks() {
        return getNumOfWarmUpTicks() + getNumOfTicksToRun();
    }

    /** @return the default attribute set assigned to agents at creation */
    public AttributeSetCollection getBaseAgentAttributeSetCollection() {
        return baseAgentAttributeSetCollection;
    }

    /** @return the default attribute set assigned to the environment */
    public AttributeSetCollection getBaseEnvironmentAttributeSetCollection() {
        return baseEnvironmentAttributeSetCollection;
    }

    /** @return true if processes are synchronised during simulation */
    public boolean getAreProcessesSynced() {
        return areProcessesSynced;
    }

    /** @return true if agent stores are configured to hold agent copies */
    public boolean getDoAgentStoresHoldAgentCopies() {
        return doAgentStoresHoldAgentCopies;
    }

    /** @return true if caching is enabled for agent lookups */
    public boolean getIsCacheUsed() {
        return isCacheUsed;
    }

    /** @return true if attribute set results are stored on disk, false if attribute set results are stored in memory */
    public boolean getAreAttributeSetResultsStoredOnDisk() {
        return areAttributeSetResultsStoredOnDisk;
    }

    /** @return a new results instance used to process and store simulation output */
    public Results getResults() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (results != null)
            return DeepCopier.deepCopy(results, new TypeToken<Results>() {}.getType());
        return resultsClass.getDeclaredConstructor().newInstance();
    }

    /** @return the agent generator assigned to this model run */
    public AgentGenerator getAgentGenerator() {
        return agentGenerator;
    }

    /** @return the environment generator assigned to this model run */
    public EnvironmentGenerator getEnvironmentGenerator() {
        return environmentGenerator;
    }

    /** @return the scheduler used for simulation progression */
    public ModelScheduler getModelScheduler() {
        return modelScheduler;
    }
}
