package com.omaarr90.core.engine;

import static org.junit.jupiter.api.Assertions.*;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.core.circuit.CircuitBuilder;
import com.omaarr90.core.engine.result.SimulationResult;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for quantum circuit simulation using the Bell state.
 *
 * <p>These tests verify that the simulation engines can correctly execute quantum circuits and
 * produce expected measurement outcomes.
 */
class BellCircuitTest {

    @Test
    void testBellCircuitWithStatevectorEngine() {
        // Build a 2-qubit Bell circuit: H(0), CX(0,1), measure both qubits
        Circuit bellCircuit = CircuitBuilder.of(2).h(0).cx(0, 1).measureAll().build();

        // Get the statevector engine
        SimulatorEngine engine = SimulatorEngineRegistry.get("statevector");

        // Run the circuit
        SimulationResult result = engine.run(bellCircuit);

        // Verify basic properties
        assertNotNull(result, "Simulation result should not be null");
        assertNotNull(result.counts(), "Counts should not be null");
        assertEquals(1, result.totalShots(), "Should have 1 shot for single execution");

        Map<String, Long> counts = result.counts();

        // For a Bell state, we should only see "00" or "11" outcomes
        // Since this is a single shot, we'll get exactly one outcome
        assertEquals(1, counts.size(), "Should have exactly one measurement outcome");

        String outcome = counts.keySet().iterator().next();
        Long count = counts.get(outcome);

        assertTrue(
                outcome.equals("00") || outcome.equals("11"),
                "Bell state should only produce '00' or '11', got: " + outcome);
        assertEquals(1L, count, "Single shot should have count of 1");

        System.out.println("[DEBUG_LOG] Bell circuit outcome: " + outcome);
    }

    @Test
    void testBellCircuitWithStabilizerEngine() {
        // Build a 2-qubit Bell circuit: H(0), CX(0,1), measure both qubits
        Circuit bellCircuit = CircuitBuilder.of(2).h(0).cx(0, 1).measureAll().build();

        // Get the stabilizer engine
        SimulatorEngine engine = SimulatorEngineRegistry.get("stabilizer");

        // Run the circuit
        SimulationResult result = engine.run(bellCircuit);

        // Verify basic properties
        assertNotNull(result, "Simulation result should not be null");
        assertNotNull(result.counts(), "Counts should not be null");
        assertEquals(1, result.totalShots(), "Should have 1 shot for single execution");

        Map<String, Long> counts = result.counts();

        // For a Bell state, we should only see "00" or "11" outcomes
        // Since this is a single shot, we'll get exactly one outcome
        assertEquals(1, counts.size(), "Should have exactly one measurement outcome");

        String outcome = counts.keySet().iterator().next();
        Long count = counts.get(outcome);

        assertTrue(
                outcome.equals("00") || outcome.equals("11"),
                "Bell state should only produce '00' or '11', got: " + outcome);
        assertEquals(1L, count, "Single shot should have count of 1");

        System.out.println("[DEBUG_LOG] Bell circuit outcome (stabilizer): " + outcome);
    }

    @Test
    void testBellCircuitMultipleRuns() {
        // Build a 2-qubit Bell circuit
        Circuit bellCircuit = CircuitBuilder.of(2).h(0).cx(0, 1).measureAll().build();

        SimulatorEngine engine = SimulatorEngineRegistry.get("statevector");

        // Run the circuit multiple times to check distribution
        int runs = 100;
        int count00 = 0;
        int count11 = 0;

        for (int i = 0; i < runs; i++) {
            SimulationResult result = engine.run(bellCircuit);
            Map<String, Long> counts = result.counts();

            String outcome = counts.keySet().iterator().next();
            if ("00".equals(outcome)) {
                count00++;
            } else if ("11".equals(outcome)) {
                count11++;
            } else {
                fail("Unexpected outcome: " + outcome);
            }
        }

        System.out.println("[DEBUG_LOG] Bell circuit distribution over " + runs + " runs:");
        System.out.println("[DEBUG_LOG] '00': " + count00 + " (" + (100.0 * count00 / runs) + "%)");
        System.out.println("[DEBUG_LOG] '11': " + count11 + " (" + (100.0 * count11 / runs) + "%)");

        // Both outcomes should occur (with high probability)
        // We allow some statistical variation but expect both outcomes
        assertTrue(count00 > 0, "Should see some '00' outcomes");
        assertTrue(count11 > 0, "Should see some '11' outcomes");
        assertEquals(runs, count00 + count11, "All outcomes should be accounted for");

        // Check that the distribution is reasonably balanced (within 3 standard deviations)
        // For a fair coin flip over 100 trials, we expect ~50 ± 15 (3σ)
        double expectedRatio = 0.5;
        double actualRatio = (double) count00 / runs;
        double tolerance = 0.15; // 3 standard deviations for 100 trials

        assertTrue(
                Math.abs(actualRatio - expectedRatio) < tolerance,
                String.format(
                        "Distribution should be roughly balanced. Got %.2f, expected %.2f ± %.2f",
                        actualRatio, expectedRatio, tolerance));
    }

    @Test
    void testSimpleHadamardCircuit() {
        // Test a simple single-qubit Hadamard circuit
        Circuit hadamardCircuit = CircuitBuilder.of(1).h(0).measureAll().build();

        SimulatorEngine engine = SimulatorEngineRegistry.get("statevector");
        SimulationResult result = engine.run(hadamardCircuit);

        assertNotNull(result);
        Map<String, Long> counts = result.counts();
        assertEquals(1, counts.size());

        String outcome = counts.keySet().iterator().next();
        assertTrue(
                outcome.equals("0") || outcome.equals("1"),
                "Hadamard should produce '0' or '1', got: " + outcome);

        System.out.println("[DEBUG_LOG] Hadamard circuit outcome: " + outcome);
    }

    @Test
    void testIdentityCircuit() {
        // Test a circuit with no gates (identity)
        Circuit identityCircuit = CircuitBuilder.of(2).measureAll().build();

        SimulatorEngine engine = SimulatorEngineRegistry.get("statevector");
        SimulationResult result = engine.run(identityCircuit);

        assertNotNull(result);
        Map<String, Long> counts = result.counts();
        assertEquals(1, counts.size());

        // Should always measure |00⟩ state
        assertTrue(counts.containsKey("00"), "Identity circuit should produce '00'");
        assertEquals(1L, counts.get("00"), "Should have count of 1");

        System.out.println("[DEBUG_LOG] Identity circuit outcome: " + counts);
    }
}
