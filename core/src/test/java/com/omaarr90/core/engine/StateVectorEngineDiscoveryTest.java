package com.omaarr90.core.engine;

import static org.junit.jupiter.api.Assertions.*;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.core.circuit.CircuitBuilder;
import com.omaarr90.core.engine.result.SimulationResult;
import com.omaarr90.core.engine.result.StateVectorResult;
import java.util.ServiceLoader;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Unit tests for StateVectorEngine ServiceLoader discovery. */
class StateVectorEngineDiscoveryTest {

    @Test
    void testStateVectorEngineIsDiscoverable() {
        Set<String> available = SimulatorEngineRegistry.available();

        assertTrue(
                available.contains("statevector"),
                "Should contain statevector engine. Available: " + available);
    }

    @Test
    void testGetStateVectorEngine() {
        SimulatorEngine engine = SimulatorEngineRegistry.get("statevector");

        assertNotNull(engine, "StateVector engine should not be null");
        assertEquals("statevector", engine.id(), "Engine ID should be 'statevector'");
        assertTrue(
                engine.getClass().getSimpleName().equals("StateVectorEngine"),
                "Engine should be instance of StateVectorEngine");
    }

    @Test
    void testServiceLoaderFindsExactlyOneStateVectorEngine() {
        ServiceLoader<SimulatorEngine> loader = ServiceLoader.load(SimulatorEngine.class);

        long stateVectorEngineCount =
                loader.stream()
                        .map(ServiceLoader.Provider::get)
                        .filter(engine -> "statevector".equals(engine.id()))
                        .count();

        assertEquals(
                1,
                stateVectorEngineCount,
                "ServiceLoader should find exactly one statevector engine");
    }

    @Test
    void testStateVectorEngineReturnsStateVectorResult() {
        SimulatorEngine engine = SimulatorEngineRegistry.get("statevector");

        // Create a simple circuit for testing
        Circuit circuit = CircuitBuilder.of(2).h(0).cx(0, 1).measure(0, 0).measure(1, 1).build();

        SimulationResult result = engine.run(circuit);

        assertNotNull(result, "Result should not be null");
        assertTrue(
                result instanceof StateVectorResult,
                "Result should be instance of StateVectorResult");

        StateVectorResult stateVectorResult = (StateVectorResult) result;
        assertEquals(1, stateVectorResult.totalShots(), "Total shots should be 1");
        assertNotNull(stateVectorResult.counts(), "Counts should not be null");
        assertFalse(stateVectorResult.counts().isEmpty(), "Counts should not be empty");
    }

    @Test
    void testStateVectorEngineWithNullCircuit() {
        SimulatorEngine engine = SimulatorEngineRegistry.get("statevector");

        assertThrows(
                NullPointerException.class,
                () -> engine.run(null),
                "Should throw NullPointerException for null circuit");
    }
}
