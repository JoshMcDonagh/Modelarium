package unit.modelarium.results;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.QuadFunction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link FunctionalResults}.
 *
 * <p>Verifies that the functional delegate methods are invoked correctly for accumulating
 * agent property and event data.</p>
 */
public class FunctionalResultsTest {

    private String lastPropertyAttrSet;
    private String lastPropertyName;
    private List<?> lastPropertyLeft;
    private List<?> lastPropertyRight;

    private String lastPreEventAttrSet;
    private String lastPreEventName;
    private List<Boolean> lastPreEventLeft;
    private List<Boolean> lastPreEventRight;

    private String lastPostEventAttrSet;
    private String lastPostEventName;
    private List<Boolean> lastPostEventLeft;
    private List<Boolean> lastPostEventRight;

    private FunctionalResults results;

    @BeforeEach
    void setUp() {
        QuadFunction<String, String, List<?>, List<?>, List<?>> propertyAccumulator =
                (attrSet, name, a, b) -> {
                    lastPropertyAttrSet = attrSet;
                    lastPropertyName = name;
                    lastPropertyLeft = a;
                    lastPropertyRight = b;
                    List<Object> combined = new ArrayList<>();
                    combined.addAll(a);
                    combined.addAll(b);
                    return combined;
                };

        QuadFunction<String, String, List<Boolean>, List<Boolean>, List<?>> preEventAccumulator =
                (attrSet, name, a, b) -> {
                    lastPreEventAttrSet = attrSet;
                    lastPreEventName = name;
                    lastPreEventLeft = a;
                    lastPreEventRight = b;
                    List<Boolean> combined = new ArrayList<>(a);
                    combined.addAll(b);
                    return combined;
                };

        QuadFunction<String, String, List<Boolean>, List<Boolean>, List<?>> postEventAccumulator =
                (attrSet, name, a, b) -> {
                    lastPostEventAttrSet = attrSet;
                    lastPostEventName = name;
                    lastPostEventLeft = a;
                    lastPostEventRight = b;
                    List<Boolean> combined = new ArrayList<>(a);
                    combined.addAll(b);
                    return combined;
                };

        results = new FunctionalResults(propertyAccumulator, preEventAccumulator, postEventAccumulator);
    }

    private static Method findResultsMethod(String name, Class<?>... types) throws Exception {
        Class<?> c = FunctionalResults.class;
        for (Class<?> k = c; k != null; k = k.getSuperclass()) {
            try { return k.getDeclaredMethod(name, types); } catch (NoSuchMethodException ignored) {}
        }
        throw new NoSuchMethodException(name);
    }

    @Test
    void testAccumulateAgentPropertyResultsUsesProvidedFunction() throws Exception {
        List<?> acc = List.of("x", "y");
        List<?> incoming = List.of("z");

        Method accumulateAgentPropertyResultsMethod = findResultsMethod(
                "accumulateAgentPropertyResults", String.class, String.class, List.class, List.class);
        accumulateAgentPropertyResultsMethod.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<?> result = (List<?>) accumulateAgentPropertyResultsMethod.invoke(results, "attr", "prop", acc, incoming);

        assertEquals(List.of("x", "y", "z"), result);
        assertSame(acc, lastPropertyLeft);
        assertSame(incoming, lastPropertyRight);
        assertEquals("attr", lastPropertyAttrSet);
        assertEquals("prop", lastPropertyName);
    }

    @Test
    void testAccumulateAgentPreEventResultsUsesProvidedFunction() throws Exception {
        List<Boolean> acc = List.of(true);
        List<Boolean> incoming = List.of(false);

        Method accumulateAgentPreEventResultsMethod = findResultsMethod(
                "accumulateAgentPreEventResults", String.class, String.class, List.class, List.class);
        accumulateAgentPreEventResultsMethod.setAccessible(true);
        List<?> result = (List<?>) accumulateAgentPreEventResultsMethod.invoke(results, "attr", "event", acc, incoming);

        assertEquals(List.of(true, false), result);
        assertSame(acc, lastPreEventLeft);
        assertSame(incoming, lastPreEventRight);
        assertEquals("attr", lastPreEventAttrSet);
        assertEquals("event", lastPreEventName);
    }

    @Test
    void testAccumulateAgentPostEventResultsUsesProvidedFunction() throws Exception {
        List<Boolean> acc = List.of(false);
        List<Boolean> incoming = List.of(true, true);

        Method accumulateAgentPostEventResultsMethod = findResultsMethod(
                "accumulateAgentPostEventResults", String.class, String.class, List.class, List.class);
        accumulateAgentPostEventResultsMethod.setAccessible(true);
        List<?> result = (List<?>) accumulateAgentPostEventResultsMethod.invoke(results, "attr", "event", acc, incoming);

        assertEquals(List.of(false, true, true), result);
        assertSame(acc, lastPostEventLeft);
        assertSame(incoming, lastPostEventRight);
        assertEquals("attr", lastPostEventAttrSet);
        assertEquals("event", lastPostEventName);
    }
}
