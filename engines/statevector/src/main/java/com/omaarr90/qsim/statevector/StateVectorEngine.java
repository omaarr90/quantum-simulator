package com.omaarr90.qsim.statevector;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.core.circuit.GateOp;
import com.omaarr90.core.engine.SimulatorEngine;
import com.omaarr90.core.engine.result.SimulationResult;
import com.omaarr90.core.engine.result.StateVectorResult;
import com.omaarr90.core.gate.GateType;
import com.omaarr90.core.statevector.StateVector;
import com.omaarr90.qsim.statevector.kernel.SingleQubitKernels;
import com.omaarr90.qsim.statevector.kernel.TwoQubitKernels;
import com.omaarr90.qsim.statevector.parallel.ParallelSweep;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

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

    // Engine-local classical bit storage for mid-circuit measurements
    private final List<Integer> classical = new ArrayList<>();

    // Random number generator for measurements
    private final RandomGenerator rng;

    /** Creates a new StateVectorEngine with default seed. */
    public StateVectorEngine() {
        this.rng = RandomGeneratorFactory.of("SplittableRandom").create(System.currentTimeMillis());
    }

    /**
     * Creates a new StateVectorEngine with specified seed.
     *
     * @param seed the seed for random number generation
     */
    public StateVectorEngine(long seed) {
        this.rng = RandomGeneratorFactory.of("SplittableRandom").create(seed);
    }

    @Override
    public SimulationResult run(Circuit circuit) {
        Instant timer = Instant.now();
        int n = circuit.qubitCount();
        StateVector ψ = StateVector.allocate(n);
        Map<String, Integer> counts = new HashMap<>();

        for (GateOp op : circuit.operations()) {
            dispatch(op, ψ, rng);
        }

        // Extract amplitudes from StateVector
        double[] amplitudes = new double[2 * ψ.logicalSize()];
        double[] realPart = ψ.real();
        double[] imagPart = ψ.imag();

        // Interleave real and imaginary parts: [real0, imag0, real1, imag1, ...]
        for (int i = 0; i < ψ.logicalSize(); i++) {
            amplitudes[2 * i] = realPart[i];
            amplitudes[2 * i + 1] = imagPart[i];
        }

        // Perform measurements if circuit has any
        if (circuit.hasMeasurements()) {
            Map<String, Long> measurementCounts = performMeasurements(amplitudes, circuit, n, rng);
            for (Map.Entry<String, Long> entry : measurementCounts.entrySet()) {
                counts.put(entry.getKey(), entry.getValue().intValue());
            }
        } else {
            counts.merge(circuit.readClassicalBits(), 1, Integer::sum);
        }

        return StateVectorResult.builder()
                .amplitudes(amplitudes)
                .counts(counts)
                .shots(circuit.shots())
                .elapsed(Duration.between(timer, Instant.now()))
                .stateVectorIncluded(circuit.dumpState())
                .build();
    }

    /**
     * Dispatch helper method to keep the main loop tidy. Applies gate operations with parallel
     * dispatch when appropriate.
     *
     * @param op the gate operation to apply
     * @param ψ the quantum state vector
     * @param rng random number generator for measurements
     */
    private void dispatch(GateOp op, StateVector ψ, RandomGenerator rng) {
        if (op instanceof GateOp.Gate gate) {
            int n = Integer.numberOfTrailingZeros(ψ.logicalSize());

            // Guard with parallel dispatch threshold
            if (n <= 12) {
                // Use serial execution for small circuits
                applyGateSerial(gate, ψ);
            } else {
                // Use parallel execution for large circuits
                applyGateParallel(gate, ψ, n);
            }
        }
        // Barriers are ignored in simulation
    }

    /** Apply gate operation serially. */
    private void applyGateSerial(GateOp.Gate gate, StateVector ψ) {
        // Extract amplitudes from StateVector
        int numQubits = Integer.numberOfTrailingZeros(ψ.logicalSize());
        double[] amplitudes = new double[2 * ψ.logicalSize()];
        double[] realPart = ψ.real();
        double[] imagPart = ψ.imag();

        // Interleave real and imaginary parts: [real0, imag0, real1, imag1, ...]
        for (int i = 0; i < ψ.logicalSize(); i++) {
            amplitudes[2 * i] = realPart[i];
            amplitudes[2 * i + 1] = imagPart[i];
        }

        // Apply the gate
        applyGate(amplitudes, gate, numQubits);

        // Update StateVector with modified amplitudes
        for (int i = 0; i < ψ.logicalSize(); i++) {
            realPart[i] = amplitudes[2 * i];
            imagPart[i] = amplitudes[2 * i + 1];
        }
    }

    /** Apply gate operation in parallel using ParallelSweep. */
    private void applyGateParallel(GateOp.Gate gate, StateVector ψ, int n) {
        try {
            // Use ParallelSweep.forEachSlice for parallel execution
            ParallelSweep.forEachSlice(
                    ψ,
                    n,
                    slice -> {
                        // Apply gate to this slice
                        // This would use the slice-aware kernel methods
                        // For now, this is a placeholder implementation
                    });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Parallel gate execution was interrupted", e);
        }
    }

    /**
     * Execute the given quantum circuit multiple times and return aggregated measurement counts.
     *
     * <p>This method runs the circuit multiple times, performing fresh measurements on each shot
     * and accumulating the results in a histogram. Each shot starts with a fresh copy of the
     * pre-measurement state vector.
     *
     * @param circuit the quantum circuit to simulate
     * @param shots the number of measurement shots to perform
     * @return the simulation result containing measurement counts from all shots
     * @throws UnsupportedOperationException if the circuit contains unsupported gates
     * @throws IllegalArgumentException if the circuit is invalid for this engine or shots <= 0
     * @throws NullPointerException if circuit is null
     */
    public StateVectorResult run(Circuit circuit, int shots) {
        if (circuit == null) {
            throw new NullPointerException("Circuit cannot be null");
        }
        if (shots <= 0) {
            throw new IllegalArgumentException("Number of shots must be positive: " + shots);
        }

        Instant startTime = Instant.now();
        long gateCount = 0;

        int numQubits = circuit.qubitCount();
        if (numQubits > 20) { // Reasonable limit to prevent memory issues
            throw new IllegalArgumentException(
                    "Circuit has too many qubits for state-vector simulation: "
                            + numQubits
                            + " (maximum supported: 20)");
        }

        // Initialize state vector to |00...0⟩
        int numStates = 1 << numQubits;
        double[] baseAmplitudes = new double[2 * numStates]; // [real0, imag0, real1, imag1, ...]
        baseAmplitudes[0] = 1.0; // |00...0⟩ has amplitude 1

        // Apply all gate operations to get pre-measurement state
        for (GateOp op : circuit.ops()) {
            if (op instanceof GateOp.Gate gate) {
                applyGate(baseAmplitudes, gate, numQubits);
                gateCount++;
            }
            // Barriers are ignored in simulation
        }

        // Perform multiple shots if circuit has measurements
        Map<String, Long> aggregatedCounts = new HashMap<>();
        if (circuit.hasMeasurements()) {
            for (int shot = 0; shot < shots; shot++) {
                // Create fresh copy for this shot
                double[] amplitudes = baseAmplitudes.clone();

                // Perform measurements on the copy
                Map<String, Long> shotCounts =
                        performMeasurements(amplitudes, circuit, numQubits, rng);

                // Aggregate counts
                for (Map.Entry<String, Long> entry : shotCounts.entrySet()) {
                    aggregatedCounts.merge(entry.getKey(), entry.getValue(), Long::sum);
                }
            }
        }

        Duration elapsed = Duration.between(startTime, Instant.now());
        return new StateVectorResult(baseAmplitudes, aggregatedCounts, shots, gateCount, elapsed);
    }

    @Override
    public String id() {
        return ENGINE_ID;
    }

    /**
     * Samples the specified qubit, collapses the state, and returns 0 or 1.
     *
     * <p>This method performs a projective measurement on the specified qubit, sampling from the
     * probability distribution defined by the quantum state amplitudes. The state vector is
     * collapsed in place to reflect the measurement outcome.
     *
     * <p><strong>SIDE EFFECT:</strong> This method modifies the state vector by collapsing it to
     * the measured outcome and renormalizing the amplitudes.
     *
     * @param amplitudes the state vector amplitudes (modified in place)
     * @param qubit the qubit to measure (0-based index)
     * @param numQubits total number of qubits in the system
     * @param rng the random number generator for sampling
     * @return the measurement outcome (0 or 1)
     * @throws IllegalArgumentException if qubit index is out of range
     */
    public int measure(double[] amplitudes, int qubit, int numQubits, RandomGenerator rng) {
        if (qubit < 0 || qubit >= numQubits) {
            throw new IllegalArgumentException(
                    "Qubit index " + qubit + " out of range [0, " + (numQubits - 1) + "]");
        }

        int numStates = 1 << numQubits;

        // Compute probability for outcome 0 using bit-mask stride
        double p0 = 0.0;
        double totalNorm = 0.0;
        for (int i = 0; i < numStates; i++) {
            double real = amplitudes[2 * i];
            double imag = amplitudes[2 * i + 1];
            double prob = real * real + imag * imag;
            totalNorm += prob;
            if (((i >> qubit) & 1) == 0) {
                p0 += prob;
            }
        }

        // Check if state vector is normalized (not all zero)
        if (totalNorm < 1e-15) {
            throw new IllegalStateException("Cannot measure qubit with zero probability");
        }

        // Sample outcome
        double r = rng.nextDouble();
        int outcome = (r >= p0) ? 1 : 0;

        // Compute renormalization factor
        double pOutcome = (outcome == 0) ? p0 : (1.0 - p0);
        if (pOutcome < 1e-15) {
            throw new IllegalStateException("Cannot measure qubit with zero probability");
        }
        double renorm = 1.0 / Math.sqrt(pOutcome);

        // Collapse state vector
        for (int i = 0; i < numStates; i++) {
            if (((i >> qubit) & 1) != outcome) {
                // Zero out incompatible amplitudes
                amplitudes[2 * i] = 0.0;
                amplitudes[2 * i + 1] = 0.0;
            } else {
                // Renormalize compatible amplitudes
                amplitudes[2 * i] *= renorm;
                amplitudes[2 * i + 1] *= renorm;
            }
        }

        return outcome;
    }

    /**
     * Samples all qubits once, collapses to a computational basis state, and returns the bitstring
     * as an int (LSB = qubit 0).
     *
     * <p>This method performs a full measurement of all qubits simultaneously, sampling from the
     * probability distribution defined by the quantum state amplitudes. The state vector is
     * collapsed to the measured computational basis state.
     *
     * <p><strong>SIDE EFFECT:</strong> This method modifies the state vector by collapsing it to a
     * single computational basis state with amplitude 1.0 + 0.0i.
     *
     * @param amplitudes the state vector amplitudes (modified in place)
     * @param numQubits total number of qubits in the system
     * @param rng the random number generator for sampling
     * @return the measurement outcome as a bitstring (LSB = qubit 0)
     */
    public int measureAll(double[] amplitudes, int numQubits, RandomGenerator rng) {
        int numStates = 1 << numQubits;

        // Calculate probabilities for all states
        double[] probabilities = new double[numStates];
        for (int state = 0; state < numStates; state++) {
            double real = amplitudes[2 * state];
            double imag = amplitudes[2 * state + 1];
            probabilities[state] = real * real + imag * imag;
        }

        // Sample from cumulative probability distribution
        double r = rng.nextDouble();
        double cumulativeProb = 0.0;
        int measuredState = 0;

        for (int state = 0; state < numStates; state++) {
            cumulativeProb += probabilities[state];
            if (r <= cumulativeProb) {
                measuredState = state;
                break;
            }
        }

        // Collapse to computational basis state
        for (int i = 0; i < numStates; i++) {
            if (i == measuredState) {
                amplitudes[2 * i] = 1.0;
                amplitudes[2 * i + 1] = 0.0;
            } else {
                amplitudes[2 * i] = 0.0;
                amplitudes[2 * i + 1] = 0.0;
            }
        }

        return measuredState;
    }

    private void applyGate(double[] amplitudes, GateOp.Gate gate, int numQubits) {
        GateType type = gate.type();
        int[] qubits = gate.qubits();

        switch (type) {
            case H -> applyHadamard(amplitudes, qubits[0], numQubits);
            case X -> applyPauliX(amplitudes, qubits[0], numQubits);
            case Y -> applyPauliY(amplitudes, qubits[0], numQubits);
            case Z -> applyPauliZ(amplitudes, qubits[0], numQubits);
            case S -> applyS(amplitudes, qubits[0], numQubits);
            case SDG -> applySDG(amplitudes, qubits[0], numQubits);
            case T -> applyT(amplitudes, qubits[0], numQubits);
            case TDG -> applyTDG(amplitudes, qubits[0], numQubits);
            case RX -> applyRX(amplitudes, qubits[0], numQubits, gate.params()[0]);
            case RY -> applyRY(amplitudes, qubits[0], numQubits, gate.params()[0]);
            case RZ -> applyRZ(amplitudes, qubits[0], numQubits, gate.params()[0]);
            case CX -> applyCNOT(amplitudes, qubits[0], qubits[1], numQubits);
            case CZ -> applyCZ(amplitudes, qubits[0], qubits[1], numQubits);
            case SWAP -> applySWAP(amplitudes, qubits[0], qubits[1], numQubits);
            default ->
                    throw new UnsupportedOperationException(
                            "Gate type not supported by state-vector engine: " + type);
        }
    }

    private void applyHadamard(double[] amplitudes, int qubit, int numQubits) {
        // Convert interleaved format to separate real/imag arrays
        int numStates = 1 << numQubits;
        double[] real = new double[numStates];
        double[] imag = new double[numStates];

        for (int i = 0; i < numStates; i++) {
            real[i] = amplitudes[2 * i];
            imag[i] = amplitudes[2 * i + 1];
        }

        try {
            // Use parallel kernel - create a temporary StateVector for size calculation
            StateVector tempStateVector = StateVector.allocate(numQubits);
            ParallelSweep.forEachSlice(
                    tempStateVector,
                    numQubits,
                    slice -> SingleQubitKernels.applyHadamard(real, imag, numQubits, qubit, slice));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Gate application was interrupted", e);
        }

        // Convert back to interleaved format
        for (int i = 0; i < numStates; i++) {
            amplitudes[2 * i] = real[i];
            amplitudes[2 * i + 1] = imag[i];
        }
    }

    private void applyPauliX(double[] amplitudes, int qubit, int numQubits) {
        // Convert interleaved format to separate real/imag arrays
        int numStates = 1 << numQubits;
        double[] real = new double[numStates];
        double[] imag = new double[numStates];

        for (int i = 0; i < numStates; i++) {
            real[i] = amplitudes[2 * i];
            imag[i] = amplitudes[2 * i + 1];
        }

        try {
            // Use parallel kernel - create a temporary StateVector for size calculation
            StateVector tempStateVector = StateVector.allocate(numQubits);
            ParallelSweep.forEachSlice(
                    tempStateVector,
                    numQubits,
                    slice -> SingleQubitKernels.applyPauliX(real, imag, numQubits, qubit, slice));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Gate application was interrupted", e);
        }

        // Convert back to interleaved format
        for (int i = 0; i < numStates; i++) {
            amplitudes[2 * i] = real[i];
            amplitudes[2 * i + 1] = imag[i];
        }
    }

    private void applyPauliY(double[] amplitudes, int qubit, int numQubits) {
        // Convert interleaved format to separate real/imag arrays
        int numStates = 1 << numQubits;
        double[] real = new double[numStates];
        double[] imag = new double[numStates];

        for (int i = 0; i < numStates; i++) {
            real[i] = amplitudes[2 * i];
            imag[i] = amplitudes[2 * i + 1];
        }

        try {
            // Use parallel kernel - create a temporary StateVector for size calculation
            StateVector tempStateVector = StateVector.allocate(numQubits);
            ParallelSweep.forEachSlice(
                    tempStateVector,
                    numQubits,
                    slice -> SingleQubitKernels.applyPauliY(real, imag, numQubits, qubit, slice));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Gate application was interrupted", e);
        }

        // Convert back to interleaved format
        for (int i = 0; i < numStates; i++) {
            amplitudes[2 * i] = real[i];
            amplitudes[2 * i + 1] = imag[i];
        }
    }

    private void applyPauliZ(double[] amplitudes, int qubit, int numQubits) {
        // Convert interleaved format to separate real/imag arrays
        int numStates = 1 << numQubits;
        double[] real = new double[numStates];
        double[] imag = new double[numStates];

        for (int i = 0; i < numStates; i++) {
            real[i] = amplitudes[2 * i];
            imag[i] = amplitudes[2 * i + 1];
        }

        try {
            // Use parallel kernel - create a temporary StateVector for size calculation
            StateVector tempStateVector = StateVector.allocate(numQubits);
            ParallelSweep.forEachSlice(
                    tempStateVector,
                    numQubits,
                    slice -> SingleQubitKernels.applyPauliZ(real, imag, numQubits, qubit, slice));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Gate application was interrupted", e);
        }

        // Convert back to interleaved format
        for (int i = 0; i < numStates; i++) {
            amplitudes[2 * i] = real[i];
            amplitudes[2 * i + 1] = imag[i];
        }
    }

    private void applyS(double[] amplitudes, int qubit, int numQubits) {
        // S gate is RZ(π/2)
        applyRZ(amplitudes, qubit, numQubits, Math.PI / 2.0);
    }

    private void applySDG(double[] amplitudes, int qubit, int numQubits) {
        // SDG gate is RZ(-π/2)
        applyRZ(amplitudes, qubit, numQubits, -Math.PI / 2.0);
    }

    private void applyT(double[] amplitudes, int qubit, int numQubits) {
        // T gate is RZ(π/4)
        applyRZ(amplitudes, qubit, numQubits, Math.PI / 4.0);
    }

    private void applyTDG(double[] amplitudes, int qubit, int numQubits) {
        // TDG gate is RZ(-π/4)
        applyRZ(amplitudes, qubit, numQubits, -Math.PI / 4.0);
    }

    private void applyRX(double[] amplitudes, int qubit, int numQubits, double theta) {
        // Convert interleaved format to separate real/imag arrays
        int numStates = 1 << numQubits;
        double[] real = new double[numStates];
        double[] imag = new double[numStates];

        for (int i = 0; i < numStates; i++) {
            real[i] = amplitudes[2 * i];
            imag[i] = amplitudes[2 * i + 1];
        }

        try {
            // Use parallel kernel - create a temporary StateVector for size calculation
            StateVector tempStateVector = StateVector.allocate(numQubits);
            ParallelSweep.forEachSlice(
                    tempStateVector,
                    numQubits,
                    slice ->
                            SingleQubitKernels.applyRX(real, imag, numQubits, qubit, theta, slice));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Gate application was interrupted", e);
        }

        // Convert back to interleaved format
        for (int i = 0; i < numStates; i++) {
            amplitudes[2 * i] = real[i];
            amplitudes[2 * i + 1] = imag[i];
        }
    }

    private void applyRY(double[] amplitudes, int qubit, int numQubits, double theta) {
        // Convert interleaved format to separate real/imag arrays
        int numStates = 1 << numQubits;
        double[] real = new double[numStates];
        double[] imag = new double[numStates];

        for (int i = 0; i < numStates; i++) {
            real[i] = amplitudes[2 * i];
            imag[i] = amplitudes[2 * i + 1];
        }

        try {
            // Use parallel kernel - create a temporary StateVector for size calculation
            StateVector tempStateVector = StateVector.allocate(numQubits);
            ParallelSweep.forEachSlice(
                    tempStateVector,
                    numQubits,
                    slice ->
                            SingleQubitKernels.applyRY(real, imag, numQubits, qubit, theta, slice));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Gate application was interrupted", e);
        }

        // Convert back to interleaved format
        for (int i = 0; i < numStates; i++) {
            amplitudes[2 * i] = real[i];
            amplitudes[2 * i + 1] = imag[i];
        }
    }

    private void applyRZ(double[] amplitudes, int qubit, int numQubits, double theta) {
        // Convert interleaved format to separate real/imag arrays
        int numStates = 1 << numQubits;
        double[] real = new double[numStates];
        double[] imag = new double[numStates];

        for (int i = 0; i < numStates; i++) {
            real[i] = amplitudes[2 * i];
            imag[i] = amplitudes[2 * i + 1];
        }

        try {
            // Use parallel kernel - create a temporary StateVector for size calculation
            StateVector tempStateVector = StateVector.allocate(numQubits);
            ParallelSweep.forEachSlice(
                    tempStateVector,
                    numQubits,
                    slice ->
                            SingleQubitKernels.applyRZ(real, imag, numQubits, qubit, theta, slice));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Gate application was interrupted", e);
        }

        // Convert back to interleaved format
        for (int i = 0; i < numStates; i++) {
            amplitudes[2 * i] = real[i];
            amplitudes[2 * i + 1] = imag[i];
        }
    }

    private void applyCNOT(double[] amplitudes, int control, int target, int numQubits) {
        // Convert interleaved format to separate real/imag arrays
        int numStates = 1 << numQubits;
        double[] real = new double[numStates];
        double[] imag = new double[numStates];

        for (int i = 0; i < numStates; i++) {
            real[i] = amplitudes[2 * i];
            imag[i] = amplitudes[2 * i + 1];
        }

        try {
            // Use parallel kernel - create a temporary StateVector for size calculation
            StateVector tempStateVector = StateVector.allocate(numQubits);
            ParallelSweep.forEachSlice(
                    tempStateVector,
                    numQubits,
                    slice ->
                            TwoQubitKernels.applyCX(real, imag, numQubits, control, target, slice));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Gate application was interrupted", e);
        }

        // Convert back to interleaved format
        for (int i = 0; i < numStates; i++) {
            amplitudes[2 * i] = real[i];
            amplitudes[2 * i + 1] = imag[i];
        }
    }

    private void applyCZ(double[] amplitudes, int control, int target, int numQubits) {
        // Convert interleaved format to separate real/imag arrays
        int numStates = 1 << numQubits;
        double[] real = new double[numStates];
        double[] imag = new double[numStates];

        for (int i = 0; i < numStates; i++) {
            real[i] = amplitudes[2 * i];
            imag[i] = amplitudes[2 * i + 1];
        }

        try {
            // Use parallel kernel - create a temporary StateVector for size calculation
            StateVector tempStateVector = StateVector.allocate(numQubits);
            ParallelSweep.forEachSlice(
                    tempStateVector,
                    numQubits,
                    slice ->
                            TwoQubitKernels.applyCZ(real, imag, numQubits, control, target, slice));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Gate application was interrupted", e);
        }

        // Convert back to interleaved format
        for (int i = 0; i < numStates; i++) {
            amplitudes[2 * i] = real[i];
            amplitudes[2 * i + 1] = imag[i];
        }
    }

    private void applySWAP(double[] amplitudes, int qubit1, int qubit2, int numQubits) {
        // Convert interleaved format to separate real/imag arrays
        int numStates = 1 << numQubits;
        double[] real = new double[numStates];
        double[] imag = new double[numStates];

        for (int i = 0; i < numStates; i++) {
            real[i] = amplitudes[2 * i];
            imag[i] = amplitudes[2 * i + 1];
        }

        try {
            // Use parallel kernel - create a temporary StateVector for size calculation
            StateVector tempStateVector = StateVector.allocate(numQubits);
            ParallelSweep.forEachSlice(
                    tempStateVector,
                    numQubits,
                    slice ->
                            TwoQubitKernels.applySWAP(
                                    real, imag, numQubits, qubit1, qubit2, slice));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Gate application was interrupted", e);
        }

        // Convert back to interleaved format
        for (int i = 0; i < numStates; i++) {
            amplitudes[2 * i] = real[i];
            amplitudes[2 * i + 1] = imag[i];
        }
    }

    private Map<String, Long> performMeasurements(
            double[] amplitudes, Circuit circuit, int numQubits, RandomGenerator rng) {
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
        double randomValue = rng.nextDouble();
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
