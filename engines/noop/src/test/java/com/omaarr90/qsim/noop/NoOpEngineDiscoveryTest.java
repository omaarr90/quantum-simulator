package com.omaarr90.qsim.noop;

import static org.junit.jupiter.api.Assertions.*;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.core.circuit.CircuitBuilder;
import com.omaarr90.core.engine.SimulatorEngine;
import com.omaarr90.core.engine.SimulatorEngineRegistry;
import com.omaarr90.core.engine.result.SimulationResult;
import java.util.ServiceLoader;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Unit tests for NoOpEngine ServiceLoader discovery. */
class NoOpEngineDiscoveryTest {

    @Test
    void testNoOpEngineIsDiscoverable() {
        Set<String> available = SimulatorEngineRegistry.available();

        assertTrue(
                available.contains("noop"), "Should contain noop engine. Available: " + available);
    }

    @Test
    void testGetNoOpEngine() {
        SimulatorEngine engine = SimulatorEngineRegistry.get("noop");

        assertNotNull(engine, "NoOp engine should not be null");
        assertEquals("noop", engine.id(), "Engine ID should be 'noop'");
        assertTrue(engine instanceof NoOpEngine, "Engine should be instance of NoOpEngine");
    }

    @Test
    void testServiceLoaderFindsExactlyOneNoOpEngine() {
        ServiceLoader<SimulatorEngine> loader = ServiceLoader.load(SimulatorEngine.class);

        long noopEngineCount =
                loader.stream()
                        .map(ServiceLoader.Provider::get)
                        .filter(engine -> "noop".equals(engine.id()))
                        .count();

        assertEquals(1, noopEngineCount, "ServiceLoader should find exactly one noop engine");
    }

    @Test
    void testNoOpEngineReturnsEmptyResult() {
        SimulatorEngine engine = SimulatorEngineRegistry.get("noop");

        // Create a simple circuit for testing
        Circuit circuit = CircuitBuilder.of(2).h(0).cx(0, 1).measure(0, 0).measure(1, 1).build();

        SimulationResult result = engine.run(circuit);

        assertNotNull(result, "Result should not be null");
        assertSame(SimulationResult.EMPTY, result, "Should return SimulationResult.EMPTY");
        assertEquals(0, result.totalShots(), "Total shots should be 0");
        assertTrue(result.counts().isEmpty(), "Counts should be empty");
    }

    @Test
    void testNoOpEngineWithNullCircuit() {
        SimulatorEngine engine = SimulatorEngineRegistry.get("noop");

        assertThrows(
                NullPointerException.class,
                () -> engine.run(null),
                "Should throw NullPointerException for null circuit");
    }
}
