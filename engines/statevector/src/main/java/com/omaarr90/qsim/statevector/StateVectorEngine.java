package com.omaarr90.qsim.statevector;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.core.circuit.GateOp;
import com.omaarr90.core.engine.SimulatorEngine;
import com.omaarr90.core.engine.result.SimulationResult;
import com.omaarr90.core.engine.result.StateVectorResult;
import com.omaarr90.core.gate.GateType;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * State-vector quantum simulation engine.
 *
 * <p>This engine simulates quantum circuits by maintaining the full quantum state vector and
 * applying unitary transformations for each gate operation. It provides exact simulation results
 * but has exponential memory requirements (2^n complex amplitudes for n qubits).
 *
 * <p>This implementation is thread-safe and stateless. Multiple threads can safely call the {@link
 * #run(Circuit)} method concurrently.
 *
 * <p>Supported gates: H (Hadamard), X (Pauli-X), CNOT (Controlled-X) Measurements are performed by
 * sampling from the probability distribution defined by the quantum state amplitudes.
 */
public final class StateVectorEngine implements SimulatorEngine {

    private static final String ENGINE_ID = "statevector";
    private final Random random = new Random();

    @Override
    public SimulationResult run(Circuit circuit) {
        if (circuit == null) {
            throw new NullPointerException("Circuit cannot be null");
        }

        int numQubits = circuit.qubitCount();
        if (numQubits > 20) { // Reasonable limit to prevent memory issues
            throw new IllegalArgumentException(
                    "Circuit has too many qubits for state-vector simulation: "
                            + numQubits
                            + " (maximum supported: 20)");
        }

        // Initialize state vector to |00...0⟩
        int numStates = 1 << numQubits; // 2^numQubits
        double[] amplitudes = new double[2 * numStates]; // [real0, imag0, real1, imag1, ...]
        amplitudes[0] = 1.0; // |00...0⟩ has amplitude 1

        // Apply all gate operations
        for (GateOp op : circuit.ops()) {
            if (op instanceof GateOp.Gate gate) {
                applyGate(amplitudes, gate, numQubits);
            }
            // Barriers are ignored in simulation
        }

        // Perform measurements
        Map<String, Long> counts = performMeasurements(amplitudes, circuit, numQubits);

        return new StateVectorResult(amplitudes, counts, 1);
    }

    @Override
    public String id() {
        return ENGINE_ID;
    }

    private void applyGate(double[] amplitudes, GateOp.Gate gate, int numQubits) {
        GateType type = gate.type();
        int[] qubits = gate.qubits();

        switch (type) {
            case H -> applyHadamard(amplitudes, qubits[0], numQubits);
            case X -> applyPauliX(amplitudes, qubits[0], numQubits);
            case CX -> applyCNOT(amplitudes, qubits[0], qubits[1], numQubits);
            default ->
                    throw new UnsupportedOperationException(
                            "Gate type not supported by state-vector engine: " + type);
        }
    }

    private void applyHadamard(double[] amplitudes, int qubit, int numQubits) {
        int numStates = 1 << numQubits;
        double[] newAmplitudes = new double[2 * numStates];

        for (int state = 0; state < numStates; state++) {
            int realIdx = 2 * state;
            int imagIdx = 2 * state + 1;

            double real = amplitudes[realIdx];
            double imag = amplitudes[imagIdx];

            if ((state & (1 << qubit)) == 0) {
                // |0⟩ component: (|0⟩ + |1⟩) / √2
                int flippedState = state | (1 << qubit);
                int flippedRealIdx = 2 * flippedState;
                int flippedImagIdx = 2 * flippedState + 1;

                double flippedReal = amplitudes[flippedRealIdx];
                double flippedImag = amplitudes[flippedImagIdx];

                newAmplitudes[realIdx] = (real + flippedReal) / Math.sqrt(2);
                newAmplitudes[imagIdx] = (imag + flippedImag) / Math.sqrt(2);
                newAmplitudes[flippedRealIdx] = (real - flippedReal) / Math.sqrt(2);
                newAmplitudes[flippedImagIdx] = (imag - flippedImag) / Math.sqrt(2);
            }
        }

        System.arraycopy(newAmplitudes, 0, amplitudes, 0, amplitudes.length);
    }

    private void applyPauliX(double[] amplitudes, int qubit, int numQubits) {
        int numStates = 1 << numQubits;

        for (int state = 0; state < numStates; state++) {
            if ((state & (1 << qubit)) == 0) {
                // Swap amplitudes of |...0...⟩ and |...1...⟩
                int flippedState = state | (1 << qubit);

                int realIdx = 2 * state;
                int imagIdx = 2 * state + 1;
                int flippedRealIdx = 2 * flippedState;
                int flippedImagIdx = 2 * flippedState + 1;

                double tempReal = amplitudes[realIdx];
                double tempImag = amplitudes[imagIdx];

                amplitudes[realIdx] = amplitudes[flippedRealIdx];
                amplitudes[imagIdx] = amplitudes[flippedImagIdx];
                amplitudes[flippedRealIdx] = tempReal;
                amplitudes[flippedImagIdx] = tempImag;
            }
        }
    }

    private void applyCNOT(double[] amplitudes, int control, int target, int numQubits) {
        int numStates = 1 << numQubits;

        for (int state = 0; state < numStates; state++) {
            // Apply X to target only if control is |1⟩
            if ((state & (1 << control)) != 0 && (state & (1 << target)) == 0) {
                int flippedState = state | (1 << target);

                int realIdx = 2 * state;
                int imagIdx = 2 * state + 1;
                int flippedRealIdx = 2 * flippedState;
                int flippedImagIdx = 2 * flippedState + 1;

                double tempReal = amplitudes[realIdx];
                double tempImag = amplitudes[imagIdx];

                amplitudes[realIdx] = amplitudes[flippedRealIdx];
                amplitudes[imagIdx] = amplitudes[flippedImagIdx];
                amplitudes[flippedRealIdx] = tempReal;
                amplitudes[flippedImagIdx] = tempImag;
            }
        }
    }

    private Map<String, Long> performMeasurements(
            double[] amplitudes, Circuit circuit, int numQubits) {
        if (!circuit.hasMeasurements()) {
            return Map.of();
        }

        // Calculate probabilities
        int numStates = 1 << numQubits;
        double[] probabilities = new double[numStates];
        for (int state = 0; state < numStates; state++) {
            double real = amplitudes[2 * state];
            double imag = amplitudes[2 * state + 1];
            probabilities[state] = real * real + imag * imag;
        }

        // Sample from probability distribution
        double randomValue = random.nextDouble();
        double cumulativeProb = 0.0;
        int measuredState = 0;

        for (int state = 0; state < numStates; state++) {
            cumulativeProb += probabilities[state];
            if (randomValue <= cumulativeProb) {
                measuredState = state;
                break;
            }
        }

        // Convert measured state to bit string based on measurement map
        StringBuilder bitString = new StringBuilder();
        for (int i = 0; i < circuit.classicalBits(); i++) {
            bitString.append('0'); // Initialize with zeros
        }

        for (Map.Entry<Integer, Integer> entry : circuit.measureMap().entrySet()) {
            int qubit = entry.getKey();
            int cbit = entry.getValue();
            char bit = ((measuredState & (1 << qubit)) != 0) ? '1' : '0';
            bitString.setCharAt(cbit, bit);
        }

        Map<String, Long> counts = new HashMap<>();
        counts.put(bitString.toString(), 1L);
        return counts;
    }
}
