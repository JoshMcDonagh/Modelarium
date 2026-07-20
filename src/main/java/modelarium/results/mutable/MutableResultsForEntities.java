package modelarium.results.mutable;

import modelarium.entities.Entity;
import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.contexts.Context;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.EntityLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for collecting and querying the logs of a group of entities.
 *
 * <p>This class gathers each entity's {@link EntityLog} and provides the shared machinery for counting logs,
 * retrieving them at various levels of granularity, merging results across workers and disconnecting the underlying
 * databases. It is extended by {@link MutableResultsForAgents} and {@link MutableResultsForEnvironment}.
 *
 * @param <SC> the type of simulation context the entities use
 * @param <C> the type of context interface the entities' attributes are given
 * @param <AS> the type of attribute set the entities own
 * @param <ASL> the type of attribute set log the entities produce
 */
public sealed abstract class MutableResultsForEntities<SC extends SimulationContext, C extends Context, AS extends AttributeSet<SC,C>, ASL extends AttributeSetLog<SC>>
        permits MutableResultsForAgents, MutableResultsForEnvironment {

    /** The logs of the entities these results cover */
    private final List<EntityLog<SC,C,AS,ASL>> entityLogList = new ArrayList<>();

    /** Maps each entity's name to the index of its log in the log list */
    private final Map<String, Integer> entityLogIndexMap = new HashMap<>();

    /**
     * Constructs a new results container collecting the logs of the specified entities.
     *
     * @param entities the entities whose logs the results will collect
     */
    MutableResultsForEntities(List<? extends Entity<SC,C,AS,ASL>> entities) {
        for (int i = 0; i < entities.size(); i++) {
            Entity<SC,C,AS,ASL> entity = entities.get(i);
            entityLogList.add(entity.getLog());
            entityLogIndexMap.put(entity.name(), i);
        }
    }

    /**
     * Constructs a new results container collecting the log of a single entity.
     *
     * @param entity the entity whose log the results will collect
     */
    MutableResultsForEntities(Entity<SC,C,AS,ASL> entity) {
        entityLogList.add(entity.getLog());
        entityLogIndexMap.put(entity.name(), 0);
    }

    /**
     * Merges another container's entity logs into this one, reindexing the merged entities' names.
     *
     * @param other the results container whose logs to merge into this one
     */
    public void mergeWith(MutableResultsForEntities<SC,C,AS,ASL> other) {
        int originalLogListSize = entityLogList.size();
        entityLogList.addAll(other.entityLogList);
        for (Map.Entry<String, Integer> otherIndexMapEntry : other.entityLogIndexMap.entrySet()) {
            String entityName = otherIndexMapEntry.getKey();
            int oldLogIndex = otherIndexMapEntry.getValue();
            int newLogIndex = originalLogListSize + oldLogIndex;
            entityLogIndexMap.put(entityName, newLogIndex);
        }
    }

    /**
     * Retrieves an entity's log by the entity's name.
     *
     * @param entityName the name of the entity whose log to retrieve
     * @return the entity's {@link EntityLog} instance
     */
    private EntityLog<SC,C,AS,ASL> getEntityLog(String entityName) {
        return entityLogList.get(entityLogIndexMap.get(entityName));
    }

    /**
     * Retrieves the values logged for a single attribute of an entity.
     *
     * @param entityName the name of the entity the attribute belongs to
     * @param attributeSetName the name of the attribute set the attribute belongs to
     * @param attributeName the name of the attribute whose values to retrieve
     * @return the attribute's logged values, one per tick
     */
    protected List<Object> getLogsForEntityAttribute(String entityName, String attributeSetName, String attributeName) {
        return getEntityLog(entityName).get(attributeSetName).getValues(attributeName);
    }

    /**
     * Returns the number of entities with logs in these results.
     *
     * @return the entity log count
     */
    protected int entityLogCount() {
        return entityLogList.size();
    }

    /**
     * Returns the number of attribute set logs recorded for the named entity.
     *
     * @param entityName the name of the entity whose logs to count
     * @return the entity's attribute set log count
     */
    protected int entityAttributeSetLogCount(String entityName) {
        return getEntityLog(entityName).attributeSetLogCount();
    }

    /**
     * Returns the number of attribute logs recorded in the named entity's named attribute set.
     *
     * @param entityName the name of the entity whose logs to count
     * @param attributeSetName the name of the attribute set whose logs to count
     * @return the attribute set's attribute log count
     */
    protected int entityAttributeSetAttributeLogCount(String entityName, String attributeSetName) {
        return getEntityLog(entityName).get(attributeSetName).attributeLogCount();
    }

    /**
     * Retrieves the values logged for every attribute in one of an entity's attribute sets.
     *
     * @param entityName the name of the entity the attribute set belongs to
     * @param attributeSetName the name of the attribute set whose logs to retrieve
     * @return a map from attribute name to that attribute's logged values
     */
    protected Map<String, List<Object>> getLogsForEntityAttributeSetAsMap(String entityName, String attributeSetName) {
        ASL attributeSetLog = getEntityLog(entityName).get(attributeSetName);
        List<String> attributeNamesList = attributeSetLog.getAttributeNamesList();
        Map<String, List<Object>> logsForEntityAttributeSet = new HashMap<>();

        for (String attributeName : attributeNamesList)
            logsForEntityAttributeSet.put(attributeName, attributeSetLog.getValues(attributeName));

        return logsForEntityAttributeSet;
    }

    /**
     * Retrieves the values logged for every attribute of an entity.
     *
     * @param entityName the name of the entity whose logs to retrieve
     * @return a map from attribute set name to a map from attribute name to that attribute's logged values
     */
    protected Map<String, Map<String, List<Object>>> getLogsForEntityAsMap(String entityName) {
        EntityLog<SC,C,AS,ASL> entityLog = getEntityLog(entityName);
        Map<String, Map<String, List<Object>>> logsForEntity = new HashMap<>();

        for (int i = 0; i < entityLog.attributeSetLogCount(); i++) {
            ASL attributeSetLog = entityLog.get(i);
            String attributeSetName = attributeSetLog.getAttributeSetName();
            logsForEntity.put(attributeSetName, getLogsForEntityAttributeSetAsMap(entityName, attributeSetName));
        }

        return logsForEntity;
    }

    /**
     * Retrieves the values logged for every attribute of every entity in these results.
     *
     * @return a map from entity name to a map from attribute set name to a map from attribute name to that
     *         attribute's logged values
     */
    protected Map<String, Map<String, Map<String, List<Object>>>> allLogs() {
        Map<String, Map<String, Map<String, List<Object>>>> allLogs = new HashMap<>();
        for (EntityLog<SC,C,AS,ASL> entityLog : entityLogList) {
            String entityName = entityLog.getEntityName();
            allLogs.put(entityName, getLogsForEntityAsMap(entityName));
        }
        return allLogs;
    }

    /**
     * Disconnects the databases of every collected entity log and clears the log collection.
     */
    public void disconnectDatabases() {
        for (EntityLog<SC,C,AS,ASL> entityLog : entityLogList)
            entityLog.disconnectDatabases();

        entityLogList.clear();
        entityLogIndexMap.clear();
    }
}
