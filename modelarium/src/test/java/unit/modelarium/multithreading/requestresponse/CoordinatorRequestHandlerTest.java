package unit.modelarium.multithreading.requestresponse;

import modelarium.Config;
import modelarium.clock.Clock;
import modelarium.entities.readonly.ReadOnlyAgent;
import modelarium.entities.agentsets.ReadOnlyAgentSet;
import modelarium.entities.Agent;
import modelarium.entities.agentsets.AgentSet;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.Environment;
import modelarium.entities.readonly.ReadOnlyEnvironment;
import modelarium.multithreading.requestresponse.*;
import modelarium.utils.Cloners;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static unit.modelarium.multithreading.requestresponse.RequestResponseTestHelpers.syncedConfig;

public class CoordinatorRequestHandlerTest {

    @Test
    public void testAllWorkersFinishTick_ReleasesOnlyWhenAllWorkersArrive_AndResets() throws InterruptedException {
        Fixture fixture = fixture(2);
        CoordinatorRequestHandler.AllWorkersFinishTick handler = new CoordinatorRequestHandler.AllWorkersFinishTick(
                "coordinator", fixture.config, fixture.controller, fixture.agents, fixture.environment, fixture.clock
        );

        handler.handleRequest(new Request("worker_0", null, RequestType.ALL_WORKERS_FINISH_TICK, null));

        assertEquals(0, fixture.clock.currentTick());
        assertTrue(fixture.controller.getResponseQueue("worker_0").isEmpty());

        handler.handleRequest(new Request("worker_1", null, RequestType.ALL_WORKERS_FINISH_TICK, null));

        assertEquals(1, fixture.clock.currentTick());
        assertEquals(ResponseType.ALL_WORKERS_FINISH_TICK,
                fixture.controller.getResponseQueue("worker_0").take().getResponseType());
        assertEquals(ResponseType.ALL_WORKERS_FINISH_TICK,
                fixture.controller.getResponseQueue("worker_1").take().getResponseType());

        handler.handleRequest(new Request("worker_0", null, RequestType.ALL_WORKERS_FINISH_TICK, null));
        handler.handleRequest(new Request("worker_1", null, RequestType.ALL_WORKERS_FINISH_TICK, null));

        assertEquals(2, fixture.clock.currentTick());
    }


    @Test
    public void testAllWorkersUpdateCoordinator_ReleasesOnlyWhenAllWorkersArrive_AndResets() throws InterruptedException {
        Fixture fixture = fixture(2);
        CoordinatorRequestHandler.AllWorkersUpdateCoordinator handler =
                new CoordinatorRequestHandler.AllWorkersUpdateCoordinator(
                        "coordinator", fixture.config, fixture.controller, fixture.agents, fixture.environment, fixture.clock
                );

        handler.handleRequest(new Request("worker_0", null, RequestType.ALL_WORKERS_UPDATE_COORDINATOR, null));

        assertTrue(fixture.controller.getResponseQueue("worker_0").isEmpty());

        handler.handleRequest(new Request("worker_1", null, RequestType.ALL_WORKERS_UPDATE_COORDINATOR, null));

        assertEquals(ResponseType.ALL_WORKERS_UPDATE_COORDINATOR,
                fixture.controller.getResponseQueue("worker_0").take().getResponseType());
        assertEquals(ResponseType.ALL_WORKERS_UPDATE_COORDINATOR,
                fixture.controller.getResponseQueue("worker_1").take().getResponseType());

        handler.handleRequest(new Request("worker_0", null, RequestType.ALL_WORKERS_UPDATE_COORDINATOR, null));
        assertTrue(fixture.controller.getResponseQueue("worker_0").isEmpty());
        handler.handleRequest(new Request("worker_1", null, RequestType.ALL_WORKERS_UPDATE_COORDINATOR, null));
        assertFalse(fixture.controller.getResponseQueue("worker_0").isEmpty());
    }

    @Test
    public void testUpdateCoordinatorAgents_DeepCopiesIncomingAgentsIntoGlobalSet() {
        Agent incomingAgent = new Agent("incoming", List.of());
        AgentSet incoming = new AgentSet(List.of(incomingAgent));
        Fixture fixture = fixture(new AgentSet(), 2);
        CoordinatorRequestHandler.UpdateCoordinatorAgents handler =
                new CoordinatorRequestHandler.UpdateCoordinatorAgents(
                        "coordinator", fixture.config, fixture.controller, fixture.agents, fixture.environment, fixture.clock
                );

        handler.handleRequest(new Request(
                "worker",
                null,
                RequestType.UPDATE_COORDINATOR_AGENTS,
                incoming
        ));

        assertTrue(fixture.agents.doesAgentExist("incoming"));
        assertNotSame(incomingAgent, fixture.agents.get("incoming"));
    }

    @Test
    public void testAgentAccess_ReturnsRequestedReadOnlyAgent() throws InterruptedException {
        Agent target = new Agent("target", List.of());
        Fixture fixture = fixture(new AgentSet(List.of(target)), 2);
        CoordinatorRequestHandler.AgentAccess handler = new CoordinatorRequestHandler.AgentAccess(
                "coordinator", fixture.config, fixture.controller, fixture.agents, fixture.environment, fixture.clock
        );

        handler.handleRequest(new Request("worker", null, RequestType.AGENT_ACCESS, "target"));

        Response response = fixture.controller.getResponseQueue("worker").take();
        assertEquals(ResponseType.AGENT_ACCESS, response.getResponseType());
        assertInstanceOf(ReadOnlyAgent.class, response.getPayload());
        assertEquals("target", ((ReadOnlyAgent) response.getPayload()).name());
    }

    @Test
    public void testAgentAccess_NonStringPayload_IllegalArgumentException() {
        Fixture fixture = fixture(2);
        CoordinatorRequestHandler.AgentAccess handler = new CoordinatorRequestHandler.AgentAccess(
                "coordinator", fixture.config, fixture.controller, fixture.agents, fixture.environment, fixture.clock
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> handler.handleRequest(new Request("worker", null, RequestType.AGENT_ACCESS, 123))
        );
    }

    @Test
    public void testGlobalAgentSetAccess_ReturnsEntireReadOnlyGlobalSetIncludingDeadAgents() throws InterruptedException {
        Agent alive = new Agent("alive", List.of());
        Agent dead = new Agent("dead", List.of());
        dead.kill();
        Fixture fixture = fixture(new AgentSet(List.of(alive, dead)), 2);
        CoordinatorRequestHandler.GlobalAgentSetAccess handler =
                new CoordinatorRequestHandler.GlobalAgentSetAccess(
                        "coordinator", fixture.config, fixture.controller, fixture.agents, fixture.environment, fixture.clock
                );

        handler.handleRequest(new Request("worker", null, RequestType.AGENT_SET_ACCESS, null));

        Response response = fixture.controller.getResponseQueue("worker").take();
        assertEquals(ResponseType.AGENT_SET_ACCESS, response.getResponseType());
        assertSame(fixture.agents.getAsImmutable(), response.getPayload());
        ReadOnlyAgentSet returned = (ReadOnlyAgentSet) response.getPayload();
        assertEquals(2, returned.size());
        assertFalse(returned.get("alive").isDead());
        assertTrue(returned.get("dead").isDead());
    }

    @Test
    public void testEnvironmentAttributesAccess_ReturnsReadOnlyEnvironment() throws InterruptedException {
        Fixture fixture = fixture(2);
        CoordinatorRequestHandler.EnvironmentAttributesAccess handler =
                new CoordinatorRequestHandler.EnvironmentAttributesAccess(
                        "coordinator", fixture.config, fixture.controller, fixture.agents, fixture.environment, fixture.clock
                );

        handler.handleRequest(new Request("worker", null, RequestType.ENVIRONMENT_ATTRIBUTES_ACCESS, null));

        Response response = fixture.controller.getResponseQueue("worker").take();
        assertEquals(ResponseType.ENVIRONMENT_ATTRIBUTES_ACCESS, response.getResponseType());
        assertInstanceOf(ReadOnlyEnvironment.class, response.getPayload());
        assertEquals(fixture.environment.name(), ((ReadOnlyEnvironment) response.getPayload()).name());
    }

    @Test
    public void testKillAgent_KillsNamedAgent() throws InterruptedException {
        Agent target = new Agent("target", List.of());
        Fixture fixture = fixture(new AgentSet(List.of(target)), 2);
        CoordinatorRequestHandler.KillAgent handler = new CoordinatorRequestHandler.KillAgent(
                "coordinator", fixture.config, fixture.controller, fixture.agents, fixture.environment, fixture.clock
        );

        handler.handleRequest(new Request("worker", null, RequestType.KILL_AGENT, "target"));

        assertTrue(target.isDead());
    }

    @Test
    public void testKillAgent_InvalidPayload_IllegalArgumentException() {
        Fixture fixture = fixture(2);
        CoordinatorRequestHandler.KillAgent handler = new CoordinatorRequestHandler.KillAgent(
                "coordinator", fixture.config, fixture.controller, fixture.agents, fixture.environment, fixture.clock
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> handler.handleRequest(new Request("worker", null, RequestType.KILL_AGENT, List.of("A")))
        );
    }

    @Test
    public void testKillAgents_KillsEveryNamedAgent() throws InterruptedException {
        Agent first = new Agent("first", List.of());
        Agent second = new Agent("second", List.of());
        Fixture fixture = fixture(new AgentSet(List.of(first, second)), 2);
        CoordinatorRequestHandler.KillAgents handler = new CoordinatorRequestHandler.KillAgents(
                "coordinator", fixture.config, fixture.controller, fixture.agents, fixture.environment, fixture.clock
        );

        handler.handleRequest(new Request(
                "worker",
                null,
                RequestType.KILL_AGENTS,
                List.of("first", "second")
        ));

        assertTrue(first.isDead());
        assertTrue(second.isDead());
    }

    @Test
    public void testKillAgents_ListContainingNonString_IllegalArgumentException() {
        Fixture fixture = fixture(2);
        CoordinatorRequestHandler.KillAgents handler = new CoordinatorRequestHandler.KillAgents(
                "coordinator", fixture.config, fixture.controller, fixture.agents, fixture.environment, fixture.clock
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> handler.handleRequest(new Request(
                        "worker",
                        null,
                        RequestType.KILL_AGENTS,
                        List.of("A", 2)
                ))
        );
    }

    private static Fixture fixture(int threadCount) {
        return fixture(new AgentSet(), threadCount);
    }

    private static Fixture fixture(AgentSet agentSet, int threadCount) {
        Config config = syncedConfig(Math.max(1, agentSet.size()), 10, threadCount);
        RequestResponseController controller = new RequestResponseController(config);
        Environment environment = spy(new Environment("environment", List.of()));
        EnvironmentSimulationContext environmentSimulationContext = mock(EnvironmentSimulationContext.class);
        doReturn(environmentSimulationContext).when(environment).context();
        when(environmentSimulationContext.getLocalAgentSet()).thenReturn(Cloners.standard().deepClone(agentSet));
        Clock clock = new Clock(config.tickCount());
        return new Fixture(config, controller, agentSet, environment, clock);
    }

    private record Fixture(
            Config config,
            RequestResponseController controller,
            AgentSet agents,
            Environment environment,
            Clock clock
    ) {}
}
