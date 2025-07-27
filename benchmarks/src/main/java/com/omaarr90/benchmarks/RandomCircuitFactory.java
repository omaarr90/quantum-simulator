package com.omaarr90.benchmarks;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.core.circuit.CircuitBuilder;
import com.omaarr90.core.gate.GateType;
import java.util.Random;

/**
 * Factory for generating random quantum circuits for benchmarking.
 *
 * <p>This class generates circuits with uniform random gate sequences to eliminate cache effects
 * and provide consistent benchmarking conditions. Each circuit is generated with a specific seed
 * to ensure reproducibility within a benchmark iteration.
 *
 * <p><strong>PERFORMANCE NOTE:</strong> Circuits are generated fresh for each benchmark iteration
 * to prevent JIT optimizations from skewing results due to repeated identical gate sequences.
 */
public final class RandomCircuitFactory {

    private static final GateType[] GATE_TYPES = {GateType.H, GateType.X, GateType.CX};
    
    // Private constructor - utility class
    private RandomCircuitFactory() {}

    /**
     * Generates a random quantum circuit with the specified parameters.
     *
     * <p>The circuit contains a uniform random sequence of gates:
     * <ul>
     *   <li>Single-qubit gates (H, X) are applied to random qubits
     *   <li>Two-qubit gates (CX) are applied to random qubit pairs
     *   <li>Gate distribution is approximately uniform across all gate types
     * </ul>
     *
     * @param qubits the number of qubits in the circuit
     * @param gates the number of gates to generate
     * @param seed the random seed for reproducible generation
     * @return a randomly generated quantum circuit
     * @throws IllegalArgumentException if qubits < 1 or gates < 1
     */
    public static Circuit generate(int qubits, int gates, long seed) {
        if (qubits < 1) {
            throw new IllegalArgumentException("Number of qubits must be at least 1: " + qubits);
        }
        if (gates < 1) {
            throw new IllegalArgumentException("Number of gates must be at least 1: " + gates);
        }

        Random random = new Random(seed);
        CircuitBuilder builder = CircuitBuilder.of(qubits);

        for (int i = 0; i < gates; i++) {
            GateType gateType = GATE_TYPES[random.nextInt(GATE_TYPES.length)];
            
            switch (gateType) {
                case H -> {
                    int qubit = random.nextInt(qubits);
                    builder.h(qubit);
                }
                case X -> {
                    int qubit = random.nextInt(qubits);
                    builder.x(qubit);
                }
                case CX -> {
                    int control = random.nextInt(qubits);
                    int target = random.nextInt(qubits);
                    // Ensure control and target are different
                    while (target == control) {
                        target = random.nextInt(qubits);
                    }
                    builder.cx(control, target);
                }
                default -> throw new IllegalStateException("Unsupported gate type: " + gateType);
            }
        }

        return builder.build();
    }

    /**
     * Generates a random circuit with a time-based seed.
     *
     * <p>This method uses the current system time as the seed, providing different
     * circuits on each call. Use {@link #generate(int, int, long)} for reproducible
     * circuit generation.
     *
     * @param qubits the number of qubits in the circuit
     * @param gates the number of gates to generate
     * @return a randomly generated quantum circuit
     */
    public static Circuit generate(int qubits, int gates) {
        return generate(qubits, gates, System.nanoTime());
    }
}