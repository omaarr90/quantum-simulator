package com.omaarr90.core.statevector;

import jdk.incubator.vector.DoubleVector;

/**
 * Dense state-vector container for quantum simulation using Structure-of-Arrays (SoA) layout.
 *
 * <p>This class represents a quantum state vector with 2^n complex amplitudes, where n is the
 * number of qubits. The implementation uses separate arrays for real and imaginary parts to
 * maximize performance with the JDK Vector API and SIMD operations.
 *
 * <p><strong>MEMORY LAYOUT:</strong> Uses Structure-of-Arrays (SoA) with SIMD padding:
 *
 * <ul>
 *   <li>Real and imaginary parts stored in separate {@code double[]} arrays
 *   <li>Arrays are padded to multiples of SIMD vector length for optimal performance
 *   <li>Unused tail elements are initialized to zero to prevent garbage reads
 * </ul>
 *
 * <p><strong>THREAD SAFETY:</strong> This class is not thread-safe. Engines must orchestrate
 * synchronization externally.
 *
 * <p><strong>PERFORMANCE NOTE:</strong> Designed for high-performance quantum simulation with SIMD
 * optimizations. The SoA layout enables efficient vectorized operations on complex amplitudes.
 */
public class StateVector implements Cloneable {

    /** SIMD vector length for padding calculations. */
    private static final int VLEN = DoubleVector.SPECIES_PREFERRED.length();

    /** Real parts of complex amplitudes (Structure-of-Arrays layout). */
    private final double[] real;

    /** Imaginary parts of complex amplitudes (Structure-of-Arrays layout). */
    private final double[] imag;

    /** Number of qubits in the quantum system. */
    private final int nQubits;

    /** Logical size of the state vector (2^nQubits). */
    private final int logicalSize;

    /** Padded size for SIMD alignment (multiple of VLEN). */
    private final int paddedSize;

    /**
     * Constructs a StateVector with the specified number of qubits.
     *
     * @param nQubits the number of qubits (must be between 0 and 30 inclusive)
     * @throws IllegalArgumentException if nQubits is out of valid range
     */
    private StateVector(int nQubits) {
        if (nQubits < 0 || nQubits > 30) {
            throw new IllegalArgumentException(
                    "Number of qubits must be between 0 and 30, got: " + nQubits);
        }

        this.nQubits = nQubits;
        this.logicalSize = 1 << nQubits; // 2^nQubits
        this.paddedSize = (logicalSize + VLEN - 1) & ~(VLEN - 1); // Round up to VLEN multiple

        // Allocate padded arrays and initialize to zero
        this.real = new double[paddedSize];
        this.imag = new double[paddedSize];

        // Arrays are already zero-initialized by Java, ensuring padding is clean
    }

    /**
     * Creates a new StateVector for the specified number of qubits.
     *
     * <p>The state vector is initialized to the |0...0⟩ computational basis state, with amplitude
     * 1.0 + 0.0i for the first element and 0.0 + 0.0i for all others.
     *
     * @param nQubits the number of qubits (must be between 0 and 30 inclusive)
     * @return a new StateVector initialized to |0...0⟩
     * @throws IllegalArgumentException if nQubits is out of valid range
     */
    public static StateVector allocate(int nQubits) {
        StateVector vector = new StateVector(nQubits);
        // Initialize to |0...0⟩ state: amplitude[0] = 1.0 + 0.0i
        vector.real[0] = 1.0;
        vector.imag[0] = 0.0;
        return vector;
    }

    /**
     * Returns the bit-mask offset for flipping a specific qubit to a basis state.
     *
     * <p>This method computes the offset such that {@code amplitudeIndex ^ indexOf(qubit,
     * basisState)} flips the specified qubit to the given basis state (0 or 1).
     *
     * @param qubit the qubit index (0-based)
     * @param basisState the target basis state (0 or 1)
     * @return the bit-mask offset for the qubit flip
     * @throws IllegalArgumentException if basisState is not 0 or 1
     */
    public int indexOf(int qubit, int basisState) {
        if (basisState != 0 && basisState != 1) {
            throw new IllegalArgumentException("Basis state must be 0 or 1, got: " + basisState);
        }
        return basisState << qubit;
    }

    /**
     * Returns the logical size of the state vector.
     *
     * @return 2^nQubits, the number of complex amplitudes
     */
    public int logicalSize() {
        return logicalSize;
    }

    /**
     * Returns the padded size of the internal arrays.
     *
     * @return the padded size (multiple of SIMD vector length)
     */
    public int paddedSize() {
        return paddedSize;
    }

    /**
     * Returns the raw array of real parts.
     *
     * <p><strong>PACKAGE-PRIVATE:</strong> This method exposes internal arrays for high-performance
     * engine kernels. Use with caution.
     *
     * @return the real parts array (includes padding)
     */
    double[] real() {
        return real;
    }

    /**
     * Returns the raw array of imaginary parts.
     *
     * <p><strong>PACKAGE-PRIVATE:</strong> This method exposes internal arrays for high-performance
     * engine kernels. Use with caution.
     *
     * @return the imaginary parts array (includes padding)
     */
    double[] imag() {
        return imag;
    }

    /**
     * Creates a deep copy of this StateVector.
     *
     * <p>The returned StateVector is completely independent, with its own copies of the internal
     * arrays including padding elements.
     *
     * @return a deep copy of this StateVector
     */
    @Override
    public StateVector clone() {
        StateVector cloned = new StateVector(this.nQubits);
        // Deep copy the arrays including padding
        System.arraycopy(this.real, 0, cloned.real, 0, paddedSize);
        System.arraycopy(this.imag, 0, cloned.imag, 0, paddedSize);
        return cloned;
    }

    /**
     * Returns a debug string representation showing the first few amplitudes in polar form.
     *
     * <p>This method is intended for debugging and displays amplitudes in the format |amplitude| ∠
     * phase for the first few non-zero or significant amplitudes.
     *
     * @return a debug string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StateVector[")
                .append(nQubits)
                .append(" qubits, ")
                .append(logicalSize)
                .append(" amplitudes]:%n");

        // Show first few amplitudes or until we find some non-zero ones
        int maxShow = Math.min(8, logicalSize);
        boolean foundNonZero = false;

        for (int i = 0; i < maxShow; i++) {
            double r = real[i];
            double im = imag[i];
            double magnitude = Math.hypot(r, im);

            if (magnitude > 1e-10) { // Show significant amplitudes
                foundNonZero = true;
                double phase = Math.atan2(im, r);
                sb.append(
                        String.format(
                                "  [%d]: %.6f ∠ %.3f°%n", i, magnitude, Math.toDegrees(phase)));
            } else if (i < 4) { // Always show first few even if zero
                sb.append(String.format("  [%d]: %.6f ∠ 0.000°%n", i, magnitude));
            }
        }

        if (!foundNonZero && maxShow < logicalSize) {
            sb.append("  ... (all remaining amplitudes are zero)%n");
        } else if (maxShow < logicalSize) {
            sb.append("  ... (").append(logicalSize - maxShow).append(" more)%n");
        }

        return sb.toString();
    }
}
