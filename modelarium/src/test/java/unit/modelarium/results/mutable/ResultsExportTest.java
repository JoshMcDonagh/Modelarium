package unit.modelarium.results.mutable;

import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.attributes.sets.mutable.MutableEnvironmentAttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.results.mutable.Results;
import modelarium.results.mutable.ResultsForAgents;
import modelarium.results.mutable.ResultsForEnvironment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static unit.modelarium.results.ResultsTestHelpers.*;

public class ResultsExportTest {

    @TempDir
    Path tempDir;

    @Test
    public void testExport_CreatesExpectedAgentAndEnvironmentStructure() {
        MutableAgentAttributeSet agentSet = agentAttributeSet("Agent_0", "stats", "score");
        Agent agent = agentWithMemoryLogs("Agent_0", agentSet);
        record(agent, "stats", "score", 1.0, 2.0);

        MutableEnvironmentAttributeSet environmentSet = environmentAttributeSet(
                "environment",
                "state",
                "tick"
        );
        Environment environment = environmentWithMemoryLogs("environment", environmentSet);
        record(environment, "state", "tick", 1, 2);

        Results results = mutableResults(
                new ResultsForAgents(agentSet(agent)),
                new ResultsForEnvironment(environment)
        );

        Path exported = results.export(tempDir);

        assertTrue(Files.isDirectory(exported));
        assertTrue(Files.isRegularFile(exported.resolve("agent/Agent_0/stats.csv")));
        assertTrue(Files.isRegularFile(exported.resolve("environment/state.csv")));
    }

    @Test
    public void testExport_CsvEscapesCommaQuoteNewlineAndNull() throws IOException {
        MutableAgentAttributeSet attributeSet = agentAttributeSet("Agent_0", "stats", "value");
        Agent agent = agentWithMemoryLogs("Agent_0", attributeSet);
        record(
                agent,
                "stats",
                "value",
                "plain",
                "with,comma",
                "with\"quote",
                "line1\nline2",
                null
        );

        Environment environment = environmentWithMemoryLogs(
                "environment",
                environmentAttributeSet("environment", "state", "tick")
        );
        record(environment, "state", "tick", 1, 2, 3, 4, 5);

        Results results = mutableResults(
                agentResults(agent),
                environmentResults(environment)
        );

        Path exported = results.export(tempDir);
        String csv = Files.readString(exported.resolve("agent/Agent_0/stats.csv"));
        String newline = System.lineSeparator();
        String expected = "value" + newline
                + "plain" + newline
                + "\"with,comma\"" + newline
                + "\"with\"\"quote\"" + newline
                + "\"line1\nline2\"" + newline
                + newline;

        assertEquals(expected, csv);
    }

    @Test
    public void testExport_ReturnedPathIsInsideRequestedDirectory() {
        Agent agent = agentWithLoggedProperty("Agent_0", "stats", "score");
        record(agent, "stats", "score", 1.0);
        Environment environment = environmentWithLoggedProperty("environment", "state", "tick");
        record(environment, "state", "tick", 1);
        Results results = mutableResults(agentResults(agent), environmentResults(environment));

        Path exported = results.export(tempDir);

        assertEquals(tempDir.toAbsolutePath(), exported.getParent());
        assertTrue(exported.getFileName().toString().startsWith("modelarium_results_export_-_"));
    }
}
