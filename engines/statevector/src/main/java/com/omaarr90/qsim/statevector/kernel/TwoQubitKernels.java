package com.omaarr90.qsim.statevector.kernel;

import com.omaarr90.qsim.statevector.parallel.AmplitudeSlice;
import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorSpecies;

/**
 * High-performance kernels for two-qubit quantum gate operations on state vectors.
 *
 * <p>This class provides optimized implementations of common two-qubit quantum gates using in-place
 * operations on Structure-of-Arrays (SoA) layout state vectors. The state vector is represented as
 * separate arrays for real and imaginary parts to enable efficient SIMD vectorization.
 *
 * <p><strong>PERFORMANCE NOTES:</strong>
 *
 * <ul>
 *   <li>All operations are performed in-place to minimize memory allocations
 *   <li>Uses JDK Vector API (DoubleVector) for SIMD optimizations when possible
 *   <li>Employs strided access patterns to minimize conditional branching
 *   <li>Uses bit manipulation for efficient state index calculations
 *   <li>Assumes arrays are properly aligned for optimal SIMD performance
 * </ul>
 *
 * <p><strong>THREAD SAFETY:</strong> This class contains only static methods and is thread-safe.
 * However, concurrent modifications to the same state vector arrays are not safe.
 */
public final class TwoQubitKernels {

    private static final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_PREFERRED;
    private static final int VECTOR_LENGTH = SPECIES.length();

    private TwoQubitKernels() {
        // Utility class - prevent instantiation
    }

    /**
     * Applies the Controlled-X (CNOT) gate to the specified control and target qubits.
     *
     * <p>Matrix representation (in computational basis |control,target⟩):
     *
     * <pre>
     * |00⟩ → |00⟩
     * |01⟩ → |01⟩
     * |10⟩ → |11⟩
     * |11⟩ → |10⟩
     * </pre>
     *
     * <p>Transformation: Flips the target qubit if and only if the control qubit is |1⟩.
     *
     * @param real the real parts of the state vector amplitudes
     * @param imag the imaginary parts of the state vector amplitudes
     * @param numQubits the total number of qubits in the system
     * @param control the index of the control qubit (0-based)
     * @param target the index of the target qubit (0-based)
     * @throws IllegalArgumentException if control or target qubits are out of range or equal
     */
    public static void applyCX(
            double[] real, double[] imag, int numQubits, int control, int target) {
        validateTwoQubitInputs(real, imag, numQubits, control, target);

        final int controlMask = 1 << control;
        final int targetMask = 1 << target;
        final int numStates = 1 << numQubits;

        // Process states in blocks to minimize conditional branching
        // We iterate through all combinations where control=1, target=0
        for (int state = controlMask; state < numStates; state += 2 * controlMask) {
            // Process block where control bit is set
            final int blockEnd = Math.min(state + controlMask, numStates);
            for (int i = state; i < blockEnd; i++) {
                if ((i & targetMask) == 0) {
                    final int flippedState = i | targetMask;

                    // Swap amplitudes
                    final double tempReal = real[i];
                    final double tempImag = imag[i];
                    real[i] = real[flippedState];
                    imag[i] = imag[flippedState];
                    real[flippedState] = tempReal;
                    imag[flippedState] = tempImag;
                }
            }
        }
    }

    /**
     * Applies the Controlled-Z gate to the specified control and target qubits.
     *
     * <p>Matrix representation (in computational basis |control,target⟩):
     *
     * <pre>
     * |00⟩ → |00⟩
     * |01⟩ → |01⟩
     * |10⟩ → |10⟩
     * |11⟩ → -|11⟩
     * </pre>
     *
     * <p>Transformation: Applies a phase flip (multiplies by -1) if and only if both control and
     * target qubits are |1⟩.
     *
     * @param real the real parts of the state vector amplitudes
     * @param imag the imaginary parts of the state vector amplitudes
     * @param numQubits the total number of qubits in the system
     * @param control the index of the control qubit (0-based)
     * @param target the index of the target qubit (0-based)
     * @throws IllegalArgumentException if control or target qubits are out of range or equal
     */
    public static void applyCZ(
            double[] real, double[] imag, int numQubits, int control, int target) {
        validateTwoQubitInputs(real, imag, numQubits, control, target);

        final int controlMask = 1 << control;
        final int targetMask = 1 << target;
        final int bothMask = controlMask | targetMask;
        final int numStates = 1 << numQubits;

        // Process states in blocks to minimize conditional branching
        // We iterate through all combinations where both control=1 and target=1
        for (int state = bothMask;
                state < numStates;
                state += 2 * Math.max(controlMask, targetMask)) {
            // Process block where the higher bit is set
            final int blockEnd = Math.min(state + Math.max(controlMask, targetMask), numStates);
            for (int i = state; i < blockEnd; i++) {
                if ((i & bothMask) == bothMask) {
                    // Apply phase flip
                    real[i] = -real[i];
                    imag[i] = -imag[i];
                }
            }
        }
    }

    /**
     * Applies the SWAP gate to exchange the states of two qubits.
     *
     * <p>Matrix representation (in computational basis |qubit1,qubit2⟩):
     *
     * <pre>
     * |00⟩ → |00⟩
     * |01⟩ → |10⟩
     * |10⟩ → |01⟩
     * |11⟩ → |11⟩
     * </pre>
     *
     * <p>Transformation: Exchanges the amplitudes where the two qubits have different values.
     *
     * @param real the real parts of the state vector amplitudes
     * @param imag the imaginary parts of the state vector amplitudes
     * @param numQubits the total number of qubits in the system
     * @param control the index of the first qubit to swap (0-based)
     * @param target the index of the second qubit to swap (0-based)
     * @throws IllegalArgumentException if control or target qubits are out of range or equal
     */
    public static void applySWAP(
            double[] real, double[] imag, int numQubits, int control, int target) {
        validateTwoQubitInputs(real, imag, numQubits, control, target);

        final int controlMask = 1 << control;
        final int targetMask = 1 << target;
        final int numStates = 1 << numQubits;

        // Process states in blocks to minimize conditional branching
        // We iterate through states where control=0,target=1 and swap with control=1,target=0
        for (int state = targetMask; state < numStates; state += 2 * targetMask) {
            // Process block where target bit is set
            final int blockEnd = Math.min(state + targetMask, numStates);
            for (int i = state; i < blockEnd; i++) {
                if ((i & controlMask) == 0) {
                    // Calculate the swapped state: control=1, target=0
                    final int swappedState = (i | controlMask) & ~targetMask;

                    // Swap amplitudes
                    final double tempReal = real[i];
                    final double tempImag = imag[i];
                    real[i] = real[swappedState];
                    imag[i] = imag[swappedState];
                    real[swappedState] = tempReal;
                    imag[swappedState] = tempImag;
                }
            }
        }
    }

    // Slice-aware versions for parallel execution

    /**
     * Applies the Controlled-X (CNOT) gate to the specified control and target qubits within an
     * amplitude slice.
     *
     * <p>This method is optimized for parallel execution by only processing states within the
     * specified slice boundaries.
     *
     * @param real the real parts of the state vector amplitudes
     * @param imag the imaginary parts of the state vector amplitudes
     * @param numQubits the total number of qubits in the system
     * @param control the index of the control qubit (0-based)
     * @param target the index of the target qubit (0-based)
     * @param slice the amplitude slice to process
     * @throws IllegalArgumentException if control or target qubits are out of range or equal
     */
    public static void applyCX(
            double[] real,
            double[] imag,
            int numQubits,
            int control,
            int target,
            AmplitudeSlice slice) {
        validateTwoQubitInputs(real, imag, numQubits, control, target);

        final int controlMask = 1 << control;
        final int targetMask = 1 << target;

        // Process only states within the slice where control=1 and target=0
        for (int i = slice.start(); i < slice.end(); i++) {
            if ((i & controlMask) != 0 && (i & targetMask) == 0) {
                final int flippedState = i | targetMask;

                // Only swap if the flipped state is also within bounds
                if (flippedState < slice.end()) {
                    // Swap amplitudes
                    final double tempReal = real[i];
                    final double tempImag = imag[i];
                    real[i] = real[flippedState];
                    imag[i] = imag[flippedState];
                    real[flippedState] = tempReal;
                    imag[flippedState] = tempImag;
                }
            }
        }
    }

    /**
     * Applies the Controlled-Z gate to the specified control and target qubits within an amplitude
     * slice.
     *
     * @param real the real parts of the state vector amplitudes
     * @param imag the imaginary parts of the state vector amplitudes
     * @param numQubits the total number of qubits in the system
     * @param control the index of the control qubit (0-based)
     * @param target the index of the target qubit (0-based)
     * @param slice the amplitude slice to process
     * @throws IllegalArgumentException if control or target qubits are out of range or equal
     */
    public static void applyCZ(
            double[] real,
            double[] imag,
            int numQubits,
            int control,
            int target,
            AmplitudeSlice slice) {
        validateTwoQubitInputs(real, imag, numQubits, control, target);

        final int controlMask = 1 << control;
        final int targetMask = 1 << target;
        final int bothMask = controlMask | targetMask;

        // Process only states within the slice where both control=1 and target=1
        for (int i = slice.start(); i < slice.end(); i++) {
            if ((i & bothMask) == bothMask) {
                // Apply phase flip
                real[i] = -real[i];
                imag[i] = -imag[i];
            }
        }
    }

    /**
     * Applies the SWAP gate to exchange the states of two qubits within an amplitude slice.
     *
     * @param real the real parts of the state vector amplitudes
     * @param imag the imaginary parts of the state vector amplitudes
     * @param numQubits the total number of qubits in the system
     * @param control the index of the first qubit to swap (0-based)
     * @param target the index of the second qubit to swap (0-based)
     * @param slice the amplitude slice to process
     * @throws IllegalArgumentException if control or target qubits are out of range or equal
     */
    public static void applySWAP(
            double[] real,
            double[] imag,
            int numQubits,
            int control,
            int target,
            AmplitudeSlice slice) {
        validateTwoQubitInputs(real, imag, numQubits, control, target);

        final int controlMask = 1 << control;
        final int targetMask = 1 << target;

        // Process only states within the slice where control=0,target=1 and swap with
        // control=1,target=0
        for (int i = slice.start(); i < slice.end(); i++) {
            if ((i & controlMask) == 0 && (i & targetMask) != 0) {
                // Calculate the swapped state: control=1, target=0
                final int swappedState = (i | controlMask) & ~targetMask;

                // Only swap if the swapped state is also within bounds
                if (swappedState >= slice.start() && swappedState < slice.end()) {
                    // Swap amplitudes
                    final double tempReal = real[i];
                    final double tempImag = imag[i];
                    real[i] = real[swappedState];
                    imag[i] = imag[swappedState];
                    real[swappedState] = tempReal;
                    imag[swappedState] = tempImag;
                }
            }
        }
    }

    /**
     * Validates input parameters for two-qubit gate operations.
     *
     * @param real the real parts array
     * @param imag the imaginary parts array
     * @param numQubits the number of qubits
     * @param control the control qubit index
     * @param target the target qubit index
     * @throws IllegalArgumentException if any parameter is invalid
     */
    private static void validateTwoQubitInputs(
            double[] real, double[] imag, int numQubits, int control, int target) {
        if (real == null || imag == null) {
            throw new IllegalArgumentException("State vector arrays cannot be null");
        }

        if (real.length != imag.length) {
            throw new IllegalArgumentException(
                    "Real and imaginary arrays must have the same length");
        }

        if (numQubits < 2) {
            throw new IllegalArgumentException(
                    "Number of qubits must be at least 2 for two-qubit gates");
        }

        final int expectedLength = 1 << numQubits;
        if (real.length != expectedLength) {
            throw new IllegalArgumentException(
                    String.format(
                            "Array length %d does not match expected length %d for %d qubits",
                            real.length, expectedLength, numQubits));
        }

        if (control < 0 || control >= numQubits) {
            throw new IllegalArgumentException(
                    String.format(
                            "Control qubit index %d is out of range [0, %d)", control, numQubits));
        }

        if (target < 0 || target >= numQubits) {
            throw new IllegalArgumentException(
                    String.format(
                            "Target qubit index %d is out of range [0, %d)", target, numQubits));
        }

        if (control == target) {
            throw new IllegalArgumentException("Control and target qubits must be different");
        }
    }
}
