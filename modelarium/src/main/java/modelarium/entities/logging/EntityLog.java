package modelarium.entities.logging;

import modelarium.entities.attributes.sets.mutable.MutableAttributeSet;
import modelarium.entities.contexts.Context;
import modelarium.entities.contexts.SimulationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for collecting the attribute set logs of a single entity.
 *
 * <p>This class gathers the {@link AttributeSetLog} of each of an entity's attribute sets, providing lookup by index
 * or name along with the ability to disconnect every underlying log database at once.
 *
 * @param <SC> the type of simulation context the entity uses
 * @param <C> the type of context interface the entity's attributes are given
 * @param <AS> the type of attribute set the entity owns
 * @param <ASL> the type of attribute set log the entity produces
 */
public class EntityLog<SC extends SimulationContext, C extends Context, AS extends MutableAttributeSet<SC,C>, ASL extends AttributeSetLog<SC>> {

    /** The name of the entity this log belongs to */
    private final String entityName;

    /** The attribute set logs this entity log collects */
    private final List<ASL> attributeSetLogList = new ArrayList<>();

    /** Maps each attribute set's name to the index of its log in the log list */
    private final Map<String, Integer> attributeSetLogIndexList = new HashMap<>();

    /**
     * Constructs a new entity log collecting the logs of the specified attribute sets.
     *
     * @param entityName the name of the entity the log belongs to
     * @param attributeSets the attribute sets whose logs the entity log will collect
     */
    public EntityLog(String entityName, List<AS> attributeSets) {
        this.entityName = entityName;
        for (int i = 0; i < attributeSets.size(); i++) {
            AS attributeSet = attributeSets.get(i);
            // noinspection unchecked
            attributeSetLogList.add((ASL) attributeSet.getLog());
            attributeSetLogIndexList.put(attributeSet.name(), i);
        }
    }

    /**
     * Returns the name of the entity this log belongs to.
     *
     * @return the owning entity's name
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Retrieves an attribute set log by index.
     *
     * @param attributeSetIndex the index of the attribute set log to retrieve
     * @return the attribute set log at the specified index
     */
    public ASL get(int attributeSetIndex) {
        return attributeSetLogList.get(attributeSetIndex);
    }

    /**
     * Retrieves an attribute set log by the name of its attribute set.
     *
     * @param attributeSetName the name of the attribute set whose log to retrieve
     * @return the attribute set log with the specified name
     */
    public ASL get(String attributeSetName) {
        return get(attributeSetLogIndexList.get(attributeSetName));
    }

    /**
     * Returns the number of attribute set logs this entity log collects.
     *
     * @return the entity log's attribute set log count
     */
    public int attributeSetLogCount() {
        return attributeSetLogList.size();
    }

    /**
     * Disconnects the database of every collected attribute set log and clears the log collection.
     */
    public void disconnectDatabases() {
        for (ASL attributeSetLog : attributeSetLogList)
            attributeSetLog.disconnectDatabase();

        attributeSetLogList.clear();
        attributeSetLogIndexList.clear();
    }
}
