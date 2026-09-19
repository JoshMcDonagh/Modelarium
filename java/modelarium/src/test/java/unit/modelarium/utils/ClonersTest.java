package unit.modelarium.utils;

import modelarium.entities.attributes.routines.functional.AgentRoutineRunFunction;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.databases.factories.MemoryBasedAttributeSetLogDatabaseFactory;
import modelarium.utils.Cloners;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ClonersTest {
    @Test
    public void testStandard_ReturnsSameInstance() {
        assertSame(Cloners.standard(), Cloners.standard());
    }

    @Test
    public void testDeepClone_NestedMutableStructures() {
        List<List<String>> original = new ArrayList<>();
        original.add(new ArrayList<>(List.of("a", "b")));

        List<List<String>> cloned = Cloners.standard().deepClone(original);

        assertEquals(original, cloned);
        assertNotSame(original, cloned);
        assertNotSame(original.get(0), cloned.get(0));

        original.get(0).add("c");

        assertEquals(List.of("a", "b"), cloned.get(0));
    }

    @Test
    public void testDeepClone_ImmutableList() {
        List<List<String>> smallOriginal = List.of(new ArrayList<>(List.of("a")));
        List<Integer> largeOriginal = List.of(1, 2, 3, 4);

        List<List<String>> smallCloned = Cloners.standard().deepClone(smallOriginal);
        List<Integer> largeCloned = Cloners.standard().deepClone(largeOriginal);

        assertEquals(smallOriginal, smallCloned);
        assertNotSame(smallOriginal, smallCloned);
        assertNotSame(smallOriginal.get(0), smallCloned.get(0));
        assertEquals(largeOriginal, largeCloned);
    }

    @Test
    public void testDeepClone_ImmutableSet() {
        Set<String> smallOriginal = Set.of("a", "b");
        Set<Integer> largeOriginal = Set.of(1, 2, 3, 4);

        Set<String> smallCloned = Cloners.standard().deepClone(smallOriginal);
        Set<Integer> largeCloned = Cloners.standard().deepClone(largeOriginal);

        assertEquals(smallOriginal, smallCloned);
        assertNotSame(smallOriginal, smallCloned);
        assertEquals(largeOriginal, largeCloned);
    }

    @Test
    public void testDeepClone_ImmutableMap() {
        Map<String, List<String>> smallOriginal = Map.of("key", new ArrayList<>(List.of("a")));
        Map<String, Integer> largeOriginal = Map.of("a", 1, "b", 2, "c", 3);

        Map<String, List<String>> smallCloned = Cloners.standard().deepClone(smallOriginal);
        Map<String, Integer> largeCloned = Cloners.standard().deepClone(largeOriginal);

        assertEquals(smallOriginal, smallCloned);
        assertNotSame(smallOriginal, smallCloned);
        assertNotSame(smallOriginal.get("key"), smallCloned.get("key"));
        assertEquals(largeOriginal, largeCloned);
    }

    @Test
    public void testDeepClone_AttributeSetLogClonesToNull() {
        AttributeSetLog<AgentSimulationContext> attributeSetLog = new AttributeSetLog<>(
                "testOwner",
                "testAttributeSetName",
                new MemoryBasedAttributeSetLogDatabaseFactory(),
                List.of()
        );

        assertNull(Cloners.standard().deepClone(attributeSetLog));
    }

    @Test
    public void testDeepClone_FunctionalAttributeFunctionsAreNotCloned() {
        AgentRoutineRunFunction runFunction = (context) -> {};

        AgentRoutineRunFunction cloned = Cloners.standard().deepClone(runFunction);

        assertSame(runFunction, cloned);
    }
}
