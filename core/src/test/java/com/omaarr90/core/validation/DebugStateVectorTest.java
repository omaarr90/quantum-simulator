package com.omaarr90.core.validation;

import static org.junit.jupiter.api.Assertions.*;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.core.circuit.CircuitBuilder;
import com.omaarr90.core.engine.SimulatorEngine;
import com.omaarr90.core.engine.SimulatorEngineRegistry;
import com.omaarr90.core.engine.result.StateVectorResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Debug test to understand state vector size issues. */
class DebugStateVectorTest {

    private static SimulatorEngine engine;

    @BeforeAll
    static void setUp() {
        engine = SimulatorEngineRegistry.get("statevector");
        assertNotNull(engine, "StateVector engine should be available");
    }

    @Test
    void debugIdentityCircuit() {
        System.out.println("[DEBUG_LOG] Testing identity circuit");

        // Create 2-qubit identity circuit
        Circuit circuit = CircuitBuilder.of(2).build();

        System.out.println("[DEBUG_LOG] Circuit qubit count: " + circuit.qubitCount());
        System.out.println("[DEBUG_LOG] Circuit operations: " + circuit.operations().size());

        // Run simulation
        StateVectorResult result = (StateVectorResult) engine.run(circuit);

        System.out.println("[DEBUG_LOG] Result qubit count: " + result.qubitCount());
        System.out.println("[DEBUG_LOG] Amplitudes length: " + result.amplitudes().length);
        System.out.println(
                "[DEBUG_LOG] Expected amplitudes length for 2 qubits: " + (2 * (1 << 2)));

        // Print first few amplitudes
        double[] amplitudes = result.amplitudes();
        System.out.println("[DEBUG_LOG] First few amplitudes:");
        for (int i = 0; i < Math.min(8, amplitudes.length); i += 2) {
            if (i + 1 < amplitudes.length) {
                System.out.println(
                        "[DEBUG_LOG]   ["
                                + (i / 2)
                                + "] = "
                                + amplitudes[i]
                                + " + "
                                + amplitudes[i + 1]
                                + "i");
            }
        }
    }

    @Test
    void debugHadamardCircuit() {
        System.out.println("[DEBUG_LOG] Testing Hadamard circuit");

        // Create 1-qubit Hadamard circuit
        Circuit circuit = CircuitBuilder.of(1).h(0).build();

        System.out.println("[DEBUG_LOG] Circuit qubit count: " + circuit.qubitCount());
        System.out.println("[DEBUG_LOG] Circuit operations: " + circuit.operations().size());

        // Run simulation
        StateVectorResult result = (StateVectorResult) engine.run(circuit);

        System.out.println("[DEBUG_LOG] Result qubit count: " + result.qubitCount());
        System.out.println("[DEBUG_LOG] Amplitudes length: " + result.amplitudes().length);
        System.out.println("[DEBUG_LOG] Expected amplitudes length for 1 qubit: " + (2 * (1 << 1)));

        // Print amplitudes
        double[] amplitudes = result.amplitudes();
        System.out.println("[DEBUG_LOG] All amplitudes:");
        for (int i = 0; i < amplitudes.length; i += 2) {
            if (i + 1 < amplitudes.length) {
                System.out.println(
                        "[DEBUG_LOG]   ["
                                + (i / 2)
                                + "] = "
                                + amplitudes[i]
                                + " + "
                                + amplitudes[i + 1]
                                + "i");
            }
        }

        // Expected: |0⟩ and |1⟩ should both have amplitude 1/√2 ≈ 0.7071
        double expected = 1.0 / Math.sqrt(2.0);
        System.out.println("[DEBUG_LOG] Expected amplitude magnitude: " + expected);
    }

    @Test
    void debugHadamardCircuitWithMeasurement() {
        System.out.println(
                "[DEBUG_LOG] Testing Hadamard circuit with measurement (multi-shot path)");

        // Create 1-qubit Hadamard circuit with measurement to trigger multi-shot path
        Circuit circuit = CircuitBuilder.of(1).h(0).measureAll().build();

        System.out.println("[DEBUG_LOG] Circuit qubit count: " + circuit.qubitCount());
        System.out.println("[DEBUG_LOG] Circuit operations: " + circuit.operations().size());
        System.out.println("[DEBUG_LOG] Circuit has measurements: " + circuit.hasMeasurements());

        // Run simulation - this should use the multi-shot path internally
        StateVectorResult result = (StateVectorResult) engine.run(circuit);

        System.out.println("[DEBUG_LOG] Result qubit count: " + result.qubitCount());
        System.out.println("[DEBUG_LOG] Amplitudes length: " + result.amplitudes().length);
        System.out.println("[DEBUG_LOG] Expected amplitudes length for 1 qubit: " + (2 * (1 << 1)));
        System.out.println("[DEBUG_LOG] Measurement counts: " + result.counts());

        // Print amplitudes
        double[] amplitudes = result.amplitudes();
        System.out.println("[DEBUG_LOG] All amplitudes:");
        for (int i = 0; i < amplitudes.length; i += 2) {
            if (i + 1 < amplitudes.length) {
                System.out.println(
                        "[DEBUG_LOG]   ["
                                + (i / 2)
                                + "] = "
                                + amplitudes[i]
                                + " + "
                                + amplitudes[i + 1]
                                + "i");
            }
        }
    }

    @Test
    void debugBellCircuit() {
        System.out.println("[DEBUG_LOG] Testing Bell circuit");

        // Create Bell circuit: H(0), CX(0,1)
        Circuit circuit = CircuitBuilder.of(2).h(0).cx(0, 1).build();

        System.out.println("[DEBUG_LOG] Circuit qubit count: " + circuit.qubitCount());
        System.out.println("[DEBUG_LOG] Circuit operations: " + circuit.operations().size());

        // Run simulation
        StateVectorResult result = (StateVectorResult) engine.run(circuit);

        System.out.println("[DEBUG_LOG] Result qubit count: " + result.qubitCount());
        System.out.println("[DEBUG_LOG] Amplitudes length: " + result.amplitudes().length);
        System.out.println(
                "[DEBUG_LOG] Expected amplitudes length for 2 qubits: " + (2 * (1 << 2)));

        // Print amplitudes
        double[] amplitudes = result.amplitudes();
        System.out.println("[DEBUG_LOG] All amplitudes:");
        for (int i = 0; i < amplitudes.length; i += 2) {
            if (i + 1 < amplitudes.length) {
                System.out.println(
                        "[DEBUG_LOG]   ["
                                + (i / 2)
                                + "] = "
                                + amplitudes[i]
                                + " + "
                                + amplitudes[i + 1]
                                + "i");
            }
        }

        // Expected: |00⟩ and |11⟩ should both have amplitude 1/√2 ≈ 0.7071
        double expected = 1.0 / Math.sqrt(2.0);
        System.out.println("[DEBUG_LOG] Expected amplitude magnitude: " + expected);
    }
}
