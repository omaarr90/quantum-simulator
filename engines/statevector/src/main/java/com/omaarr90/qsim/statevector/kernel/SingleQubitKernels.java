package com.omaarr90.qsim.statevector.kernel;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorSpecies;

/**
 * High-performance kernels for single-qubit gate operations on state vectors.
 *
 * <p>This class provides vectorized, in-place implementations of common single-qubit gates
 * operating on state vectors in Structure-of-Arrays (SoA) layout with separate real and imaginary
 * component arrays.
 *
 * <p><strong>PERFORMANCE NOTES:</strong>
 *
 * <ul>
 *   <li>All operations are performed in-place to minimize memory allocations
 *   <li>Uses JDK Vector API (DoubleVector) for SIMD optimizations when possible
 *   <li>Falls back to scalar operations when vectorization is not beneficial
 *   <li>Assumes arrays are properly aligned for optimal SIMD performance
 * </ul>
 *
 * <p><strong>THREAD SAFETY:</strong> This class contains only static methods and is thread-safe.
 * However, concurrent modifications to the same state vector arrays are not safe.
 */
public final class SingleQubitKernels {

    private static final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_PREFERRED;
    private static final int VECTOR_LENGTH = SPECIES.length();

    // Pre-computed constants for better performance
    private static final double SQRT_2_INV = 1.0 / Math.sqrt(2.0);

    private SingleQubitKernels() {
        // Utility class - prevent instantiation
    }

    /**
     * Applies the Hadamard gate to the specified qubit.
     *
     * <p>Matrix: (1/√2) * [[1, 1], [1, -1]]
     *
     * <p>Transformation:
     *
     * <ul>
     *   <li>|0⟩ → (|0⟩ + |1⟩) / √2
     *   <li>|1⟩ → (|0⟩ - |1⟩) / √2
     * </ul>
     *
     * @param real the real parts of the state vector amplitudes
     * @param imag the imaginary parts of the state vector amplitudes
     * @param numQubits the total number of qubits in the system
     * @param targetQubit the index of the qubit to apply the gate to (0-based)
     * @throws IllegalArgumentException if targetQubit is out of range
     */
    public static void applyHadamard(double[] real, double[] imag, int numQubits, int targetQubit) {
        validateInputs(real, imag, numQubits, targetQubit);

        final int numStates = 1 << numQubits;
        final int qubitMask = 1 << targetQubit;

        // Process states in pairs: |...0...⟩ and |...1...⟩
        for (int state = 0; state < numStates; state += 2 * qubitMask) {
            applyHadamardBlock(real, imag, state, qubitMask);
        }
    }

    /**
     * Applies the Pauli-X gate to the specified qubit.
     *
     * <p>Matrix: [[0, 1], [1, 0]]
     *
     * <p>Transformation:
     *
     * <ul>
     *   <li>|0⟩ → |1⟩
     *   <li>|1⟩ → |0⟩
     * </ul>
     *
     * @param real the real parts of the state vector amplitudes
     * @param imag the imaginary parts of the state vector amplitudes
     * @param numQubits the total number of qubits in the system
     * @param targetQubit the index of the qubit to apply the gate to (0-based)
     * @throws IllegalArgumentException if targetQubit is out of range
     */
    public static void applyPauliX(double[] real, double[] imag, int numQubits, int targetQubit) {
        validateInputs(real, imag, numQubits, targetQubit);

        final int numStates = 1 << numQubits;
        final int qubitMask = 1 << targetQubit;

        // Process states in pairs: swap |...0...⟩ and |...1...⟩
        for (int state = 0; state < numStates; state += 2 * qubitMask) {
            applyPauliXBlock(real, imag, state, qubitMask);
        }
    }

    /**
     * Applies the Pauli-Y gate to the specified qubit.
     *
     * <p>Matrix: [[0, -i], [i, 0]]
     *
     * <p>Transformation:
     *
     * <ul>
     *   <li>|0⟩ → i|1⟩
     *   <li>|1⟩ → -i|0⟩
     * </ul>
     *
     * @param real the real parts of the state vector amplitudes
     * @param imag the imaginary parts of the state vector amplitudes
     * @param numQubits the total number of qubits in the system
     * @param targetQubit the index of the qubit to apply the gate to (0-based)
     * @throws IllegalArgumentException if targetQubit is out of range
     */
    public static void applyPauliY(double[] real, double[] imag, int numQubits, int targetQubit) {
        validateInputs(real, imag, numQubits, targetQubit);

        final int numStates = 1 << numQubits;
        final int qubitMask = 1 << targetQubit;

        // Process states in pairs: |...0...⟩ and |...1...⟩
        for (int state = 0; state < numStates; state += 2 * qubitMask) {
            applyPauliYBlock(real, imag, state, qubitMask);
        }
    }

    /**
     * Applies the Pauli-Z gate to the specified qubit.
     *
     * <p>Matrix: [[1, 0], [0, -1]]
     *
     * <p>Transformation:
     *
     * <ul>
     *   <li>|0⟩ → |0⟩
     *   <li>|1⟩ → -|1⟩
     * </ul>
     *
     * @param real the real parts of the state vector amplitudes
     * @param imag the imaginary parts of the state vector amplitudes
     * @param numQubits the total number of qubits in the system
     * @param targetQubit the index of the qubit to apply the gate to (0-based)
     * @throws IllegalArgumentException if targetQubit is out of range
     */
    public static void applyPauliZ(double[] real, double[] imag, int numQubits, int targetQubit) {
        validateInputs(real, imag, numQubits, targetQubit);

        final int numStates = 1 << numQubits;
        final int qubitMask = 1 << targetQubit;

        // Only need to negate |1⟩ states
        for (int state = qubitMask; state < numStates; state += 2 * qubitMask) {
            applyPauliZBlock(real, imag, state, qubitMask);
        }
    }

    /**
     * Applies the RX rotation gate to the specified qubit.
     *
     * <p>Matrix: cos(θ/2) * I - i sin(θ/2) * X
     *
     * <p>= [[cos(θ/2), -i sin(θ/2)], [-i sin(θ/2), cos(θ/2)]]
     *
     * @param real the real parts of the state vector amplitudes
     * @param imag the imaginary parts of the state vector amplitudes
     * @param numQubits the total number of qubits in the system
     * @param targetQubit the index of the qubit to apply the gate to (0-based)
     * @param theta the rotation angle in radians
     * @throws IllegalArgumentException if targetQubit is out of range
     */
    public static void applyRX(
            double[] real, double[] imag, int numQubits, int targetQubit, double theta) {
        validateInputs(real, imag, numQubits, targetQubit);

        final double halfTheta = theta * 0.5;
        final double cosHalf = Math.cos(halfTheta);
        final double sinHalf = Math.sin(halfTheta);

        final int numStates = 1 << numQubits;
        final int qubitMask = 1 << targetQubit;

        // Process states in pairs: |...0...⟩ and |...1...⟩
        for (int state = 0; state < numStates; state += 2 * qubitMask) {
            applyRXBlock(real, imag, state, qubitMask, cosHalf, sinHalf);
        }
    }

    /**
     * Applies the RY rotation gate to the specified qubit.
     *
     * <p>Matrix: cos(θ/2) * I - i sin(θ/2) * Y
     *
     * <p>= [[cos(θ/2), -sin(θ/2)], [sin(θ/2), cos(θ/2)]]
     *
     * @param real the real parts of the state vector amplitudes
     * @param imag the imaginary parts of the state vector amplitudes
     * @param numQubits the total number of qubits in the system
     * @param targetQubit the index of the qubit to apply the gate to (0-based)
     * @param theta the rotation angle in radians
     * @throws IllegalArgumentException if targetQubit is out of range
     */
    public static void applyRY(
            double[] real, double[] imag, int numQubits, int targetQubit, double theta) {
        validateInputs(real, imag, numQubits, targetQubit);

        final double halfTheta = theta * 0.5;
        final double cosHalf = Math.cos(halfTheta);
        final double sinHalf = Math.sin(halfTheta);

        final int numStates = 1 << numQubits;
        final int qubitMask = 1 << targetQubit;

        // Process states in pairs: |...0...⟩ and |...1...⟩
        for (int state = 0; state < numStates; state += 2 * qubitMask) {
            applyRYBlock(real, imag, state, qubitMask, cosHalf, sinHalf);
        }
    }

    /**
     * Applies the RZ rotation gate to the specified qubit.
     *
     * <p>Matrix: e^(-iθ/2) * |0⟩⟨0| + e^(iθ/2) * |1⟩⟨1|
     *
     * <p>= [[e^(-iθ/2), 0], [0, e^(iθ/2)]]
     *
     * @param real the real parts of the state vector amplitudes
     * @param imag the imaginary parts of the state vector amplitudes
     * @param numQubits the total number of qubits in the system
     * @param targetQubit the index of the qubit to apply the gate to (0-based)
     * @param theta the rotation angle in radians
     * @throws IllegalArgumentException if targetQubit is out of range
     */
    public static void applyRZ(
            double[] real, double[] imag, int numQubits, int targetQubit, double theta) {
        validateInputs(real, imag, numQubits, targetQubit);

        final double halfTheta = theta * 0.5;
        final double cosHalf = Math.cos(halfTheta);
        final double sinHalf = Math.sin(halfTheta);

        final int numStates = 1 << numQubits;
        final int qubitMask = 1 << targetQubit;

        // Apply phase to |0⟩ and |1⟩ states separately
        for (int state = 0; state < numStates; state++) {
            if ((state & qubitMask) == 0) {
                // |0⟩ state: multiply by e^(-iθ/2) = cos(θ/2) - i sin(θ/2)
                applyPhase(real, imag, state, cosHalf, -sinHalf);
            } else {
                // |1⟩ state: multiply by e^(iθ/2) = cos(θ/2) + i sin(θ/2)
                applyPhase(real, imag, state, cosHalf, sinHalf);
            }
        }
    }

    // Private helper methods for block-wise operations

    private static void applyHadamardBlock(
            double[] real, double[] imag, int baseState, int qubitMask) {
        final int blockSize = Math.min(qubitMask, real.length - baseState);

        if (blockSize >= VECTOR_LENGTH && (blockSize % VECTOR_LENGTH) == 0) {
            applyHadamardVectorized(real, imag, baseState, blockSize);
        } else {
            applyHadamardScalar(real, imag, baseState, blockSize);
        }
    }

    private static void applyHadamardVectorized(
            double[] real, double[] imag, int baseState, int blockSize) {
        final int flippedBase = baseState | (blockSize); // Assumes blockSize is the qubit mask

        for (int i = 0; i < blockSize; i += VECTOR_LENGTH) {
            // Load vectors
            var real0 = DoubleVector.fromArray(SPECIES, real, baseState + i);
            var imag0 = DoubleVector.fromArray(SPECIES, imag, baseState + i);
            var real1 = DoubleVector.fromArray(SPECIES, real, flippedBase + i);
            var imag1 = DoubleVector.fromArray(SPECIES, imag, flippedBase + i);

            // Compute Hadamard transformation
            var newReal0 = real0.add(real1).mul(SQRT_2_INV);
            var newImag0 = imag0.add(imag1).mul(SQRT_2_INV);
            var newReal1 = real0.sub(real1).mul(SQRT_2_INV);
            var newImag1 = imag0.sub(imag1).mul(SQRT_2_INV);

            // Store results
            newReal0.intoArray(real, baseState + i);
            newImag0.intoArray(imag, baseState + i);
            newReal1.intoArray(real, flippedBase + i);
            newImag1.intoArray(imag, flippedBase + i);
        }
    }

    private static void applyHadamardScalar(
            double[] real, double[] imag, int baseState, int blockSize) {
        final int flippedBase = baseState | blockSize; // Assumes blockSize is the qubit mask

        for (int i = 0; i < blockSize; i++) {
            final int idx0 = baseState + i;
            final int idx1 = flippedBase + i;

            final double real0 = real[idx0];
            final double imag0 = imag[idx0];
            final double real1 = real[idx1];
            final double imag1 = imag[idx1];

            real[idx0] = (real0 + real1) * SQRT_2_INV;
            imag[idx0] = (imag0 + imag1) * SQRT_2_INV;
            real[idx1] = (real0 - real1) * SQRT_2_INV;
            imag[idx1] = (imag0 - imag1) * SQRT_2_INV;
        }
    }

    private static void applyPauliXBlock(
            double[] real, double[] imag, int baseState, int qubitMask) {
        final int blockSize = Math.min(qubitMask, real.length - baseState);
        final int flippedBase = baseState | qubitMask;

        for (int i = 0; i < blockSize; i++) {
            final int idx0 = baseState + i;
            final int idx1 = flippedBase + i;

            // Swap amplitudes
            final double tempReal = real[idx0];
            final double tempImag = imag[idx0];
            real[idx0] = real[idx1];
            imag[idx0] = imag[idx1];
            real[idx1] = tempReal;
            imag[idx1] = tempImag;
        }
    }

    private static void applyPauliYBlock(
            double[] real, double[] imag, int baseState, int qubitMask) {
        final int blockSize = Math.min(qubitMask, real.length - baseState);
        final int flippedBase = baseState | qubitMask;

        for (int i = 0; i < blockSize; i++) {
            final int idx0 = baseState + i;
            final int idx1 = flippedBase + i;

            final double real0 = real[idx0];
            final double imag0 = imag[idx0];
            final double real1 = real[idx1];
            final double imag1 = imag[idx1];

            // Y matrix: [[0, -i], [i, 0]]
            // Matrix multiplication: [new_0, new_1] = [[0, -i], [i, 0]] * [old_0, old_1]
            // new_0 = 0*old_0 + (-i)*old_1 = -i*old_1
            // new_1 = i*old_0 + 0*old_1 = i*old_0

            // -i * (real1 + i*imag1) = -i*real1 - i²*imag1 = imag1 - i*real1
            double newReal0 = imag1;
            double newImag0 = -real1;

            // i * (real0 + i*imag0) = i*real0 + i²*imag0 = -imag0 + i*real0
            double newReal1 = -imag0;
            double newImag1 = real0;

            real[idx0] = newReal0;
            imag[idx0] = newImag0;
            real[idx1] = newReal1;
            imag[idx1] = newImag1;
        }
    }

    private static void applyPauliZBlock(
            double[] real, double[] imag, int baseState, int qubitMask) {
        final int blockSize = Math.min(qubitMask, real.length - baseState);

        for (int i = 0; i < blockSize; i++) {
            final int idx = baseState + i;
            real[idx] = -real[idx];
            imag[idx] = -imag[idx];
        }
    }

    private static void applyRXBlock(
            double[] real,
            double[] imag,
            int baseState,
            int qubitMask,
            double cosHalf,
            double sinHalf) {
        final int blockSize = Math.min(qubitMask, real.length - baseState);
        final int flippedBase = baseState | qubitMask;

        for (int i = 0; i < blockSize; i++) {
            final int idx0 = baseState + i;
            final int idx1 = flippedBase + i;

            final double real0 = real[idx0];
            final double imag0 = imag[idx0];
            final double real1 = real[idx1];
            final double imag1 = imag[idx1];

            // RX matrix: [[cos(θ/2), -i sin(θ/2)], [-i sin(θ/2), cos(θ/2)]]
            real[idx0] = cosHalf * real0 + sinHalf * imag1;
            imag[idx0] = cosHalf * imag0 - sinHalf * real1;
            real[idx1] = cosHalf * real1 + sinHalf * imag0;
            imag[idx1] = cosHalf * imag1 - sinHalf * real0;
        }
    }

    private static void applyRYBlock(
            double[] real,
            double[] imag,
            int baseState,
            int qubitMask,
            double cosHalf,
            double sinHalf) {
        final int blockSize = Math.min(qubitMask, real.length - baseState);
        final int flippedBase = baseState | qubitMask;

        for (int i = 0; i < blockSize; i++) {
            final int idx0 = baseState + i;
            final int idx1 = flippedBase + i;

            final double real0 = real[idx0];
            final double imag0 = imag[idx0];
            final double real1 = real[idx1];
            final double imag1 = imag[idx1];

            // RY matrix: [[cos(θ/2), -sin(θ/2)], [sin(θ/2), cos(θ/2)]]
            real[idx0] = cosHalf * real0 - sinHalf * real1;
            imag[idx0] = cosHalf * imag0 - sinHalf * imag1;
            real[idx1] = sinHalf * real0 + cosHalf * real1;
            imag[idx1] = sinHalf * imag0 + cosHalf * imag1;
        }
    }

    private static void applyPhase(
            double[] real, double[] imag, int index, double cosPhase, double sinPhase) {
        final double oldReal = real[index];
        final double oldImag = imag[index];

        // Multiply by e^(iφ) = cos(φ) + i sin(φ)
        real[index] = cosPhase * oldReal - sinPhase * oldImag;
        imag[index] = cosPhase * oldImag + sinPhase * oldReal;
    }

    private static void validateInputs(
            double[] real, double[] imag, int numQubits, int targetQubit) {
        if (real == null || imag == null) {
            throw new IllegalArgumentException("State vector arrays cannot be null");
        }
        if (real.length != imag.length) {
            throw new IllegalArgumentException(
                    "Real and imaginary arrays must have the same length");
        }
        if (numQubits < 1 || numQubits > 30) {
            throw new IllegalArgumentException(
                    "Number of qubits must be between 1 and 30, got: " + numQubits);
        }
        if (targetQubit < 0 || targetQubit >= numQubits) {
            throw new IllegalArgumentException(
                    "Target qubit "
                            + targetQubit
                            + " is out of range [0, "
                            + (numQubits - 1)
                            + "]");
        }
        final int expectedSize = 1 << numQubits;
        if (real.length < expectedSize) {
            throw new IllegalArgumentException(
                    "State vector arrays are too small for "
                            + numQubits
                            + " qubits. Expected at least "
                            + expectedSize
                            + ", got "
                            + real.length);
        }
    }
}
