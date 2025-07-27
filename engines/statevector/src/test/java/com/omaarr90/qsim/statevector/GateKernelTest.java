package com.omaarr90.qsim.statevector;

import static org.junit.jupiter.api.Assertions.*;

import com.omaarr90.qsim.statevector.kernel.SingleQubitKernels;
import com.omaarr90.qsim.statevector.kernel.TwoQubitKernels;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for gate kernels (amplitude verification).
 *
 * <p>Tests verify correct quantum transformations for all single- and two-qubit gates by checking
 * state amplitudes match expected results to high precision (1e-10).
 *
 * <p>Covers all 10 gates: H, X, Y, Z, RX, RY, RZ, CX, CZ, SWAP
 */
class GateKernelTest {

    private static final double EPSILON = 1e-10;
    private static final double SQRT_2_INV = 1.0 / Math.sqrt(2.0);

    // Helper Methods

    /**
     * Allocates a basis state |index⟩ for nQubits system.
     *
     * @param nQubits number of qubits
     * @param index basis state index (0 to 2^nQubits - 1)
     * @return array containing real and imaginary parts [real[], imag[]]
     */
    private double[][] allocateBasisState(int nQubits, int index) {
        int size = 1 << nQubits;
        double[] real = new double[size];
        double[] imag = new double[size];
        real[index] = 1.0;
        return new double[][] {real, imag};
    }

    /**
     * Asserts that amplitude arrays match expected values within tolerance.
     *
     * @param real actual real amplitudes
     * @param imag actual imaginary amplitudes
     * @param expectedReal expected real amplitudes
     * @param expectedImag expected imaginary amplitudes
     * @param tolerance precision tolerance
     */
    private void assertAmplitudes(
            double[] real,
            double[] imag,
            double[] expectedReal,
            double[] expectedImag,
            double tolerance) {
        assertEquals(expectedReal.length, real.length, "Real array length mismatch");
        assertEquals(expectedImag.length, imag.length, "Imaginary array length mismatch");
        assertEquals(real.length, imag.length, "Real and imaginary array length mismatch");

        for (int i = 0; i < real.length; i++) {
            assertEquals(
                    expectedReal[i],
                    real[i],
                    tolerance,
                    String.format("Real amplitude mismatch at index %d", i));
            assertEquals(
                    expectedImag[i],
                    imag[i],
                    tolerance,
                    String.format("Imaginary amplitude mismatch at index %d", i));
        }
    }

    // Single-Qubit Gate Tests

    @Test
    void testHadamardOnZero() {
        // H|0⟩ = (|0⟩ + |1⟩) / √2
        double[][] state = allocateBasisState(1, 0);
        double[] real = state[0];
        double[] imag = state[1];

        SingleQubitKernels.applyHadamard(real, imag, 1, 0);

        double[] expectedReal = {SQRT_2_INV, SQRT_2_INV};
        double[] expectedImag = {0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testHadamardOnOne() {
        // H|1⟩ = (|0⟩ - |1⟩) / √2
        double[][] state = allocateBasisState(1, 1);
        double[] real = state[0];
        double[] imag = state[1];

        SingleQubitKernels.applyHadamard(real, imag, 1, 0);

        double[] expectedReal = {SQRT_2_INV, -SQRT_2_INV};
        double[] expectedImag = {0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testPauliXOnZero() {
        // X|0⟩ = |1⟩
        double[][] state = allocateBasisState(1, 0);
        double[] real = state[0];
        double[] imag = state[1];

        SingleQubitKernels.applyPauliX(real, imag, 1, 0);

        double[] expectedReal = {0.0, 1.0};
        double[] expectedImag = {0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testPauliXOnOne() {
        // X|1⟩ = |0⟩
        double[][] state = allocateBasisState(1, 1);
        double[] real = state[0];
        double[] imag = state[1];

        SingleQubitKernels.applyPauliX(real, imag, 1, 0);

        double[] expectedReal = {1.0, 0.0};
        double[] expectedImag = {0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testPauliYOnZero() {
        // Y|0⟩ = i|1⟩
        double[][] state = allocateBasisState(1, 0);
        double[] real = state[0];
        double[] imag = state[1];

        SingleQubitKernels.applyPauliY(real, imag, 1, 0);

        double[] expectedReal = {0.0, 0.0};
        double[] expectedImag = {0.0, 1.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testPauliYOnOne() {
        // Y|1⟩ = -i|0⟩
        double[][] state = allocateBasisState(1, 1);
        double[] real = state[0];
        double[] imag = state[1];

        SingleQubitKernels.applyPauliY(real, imag, 1, 0);

        double[] expectedReal = {0.0, 0.0};
        double[] expectedImag = {-1.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testPauliZOnZero() {
        // Z|0⟩ = |0⟩
        double[][] state = allocateBasisState(1, 0);
        double[] real = state[0];
        double[] imag = state[1];

        SingleQubitKernels.applyPauliZ(real, imag, 1, 0);

        double[] expectedReal = {1.0, 0.0};
        double[] expectedImag = {0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testPauliZOnOne() {
        // Z|1⟩ = -|1⟩
        double[][] state = allocateBasisState(1, 1);
        double[] real = state[0];
        double[] imag = state[1];

        SingleQubitKernels.applyPauliZ(real, imag, 1, 0);

        double[] expectedReal = {0.0, -1.0};
        double[] expectedImag = {0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testRXRotationPiHalfOnZero() {
        // RX(π/2)|0⟩ = (|0⟩ - i|1⟩) / √2
        double[][] state = allocateBasisState(1, 0);
        double[] real = state[0];
        double[] imag = state[1];

        SingleQubitKernels.applyRX(real, imag, 1, 0, Math.PI / 2);

        double[] expectedReal = {SQRT_2_INV, 0.0};
        double[] expectedImag = {0.0, -SQRT_2_INV};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testRXRotationPiOnZero() {
        // RX(π)|0⟩ = -i|1⟩
        double[][] state = allocateBasisState(1, 0);
        double[] real = state[0];
        double[] imag = state[1];

        SingleQubitKernels.applyRX(real, imag, 1, 0, Math.PI);

        double[] expectedReal = {0.0, 0.0};
        double[] expectedImag = {0.0, -1.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testRYRotationPiHalfOnZero() {
        // RY(π/2)|0⟩ = (|0⟩ + |1⟩) / √2
        double[][] state = allocateBasisState(1, 0);
        double[] real = state[0];
        double[] imag = state[1];

        SingleQubitKernels.applyRY(real, imag, 1, 0, Math.PI / 2);

        double[] expectedReal = {SQRT_2_INV, SQRT_2_INV};
        double[] expectedImag = {0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testRYRotationPiOnZero() {
        // RY(π)|0⟩ = |1⟩
        double[][] state = allocateBasisState(1, 0);
        double[] real = state[0];
        double[] imag = state[1];

        SingleQubitKernels.applyRY(real, imag, 1, 0, Math.PI);

        double[] expectedReal = {0.0, 1.0};
        double[] expectedImag = {0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testRZRotationPiHalfOnZero() {
        // RZ(π/2)|0⟩ = e^(-iπ/4)|0⟩
        double[][] state = allocateBasisState(1, 0);
        double[] real = state[0];
        double[] imag = state[1];

        SingleQubitKernels.applyRZ(real, imag, 1, 0, Math.PI / 2);

        double expectedReal = Math.cos(-Math.PI / 4);
        double expectedImag = Math.sin(-Math.PI / 4);
        double[] expectedRealArray = {expectedReal, 0.0};
        double[] expectedImagArray = {expectedImag, 0.0};
        assertAmplitudes(real, imag, expectedRealArray, expectedImagArray, EPSILON);
    }

    @Test
    void testRZRotationPiOnOne() {
        // RZ(π)|1⟩ = e^(iπ/2)|1⟩ = i|1⟩
        double[][] state = allocateBasisState(1, 1);
        double[] real = state[0];
        double[] imag = state[1];

        SingleQubitKernels.applyRZ(real, imag, 1, 0, Math.PI);

        double[] expectedReal = {0.0, 0.0};
        double[] expectedImag = {0.0, 1.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    // Two-Qubit Gate Tests

    @Test
    void testCXControlZero() {
        // CX|00⟩ = |00⟩ (control=0, no flip)
        double[][] state = allocateBasisState(2, 0); // |00⟩
        double[] real = state[0];
        double[] imag = state[1];

        TwoQubitKernels.applyCX(real, imag, 2, 0, 1);

        double[] expectedReal = {1.0, 0.0, 0.0, 0.0};
        double[] expectedImag = {0.0, 0.0, 0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testCXControlOne() {
        // CX|11⟩ = |01⟩ (control=1, flip target bit 1)
        double[][] state = allocateBasisState(2, 3); // |11⟩
        double[] real = state[0];
        double[] imag = state[1];

        TwoQubitKernels.applyCX(real, imag, 2, 0, 1);

        double[] expectedReal = {0.0, 1.0, 0.0, 0.0}; // |01⟩
        double[] expectedImag = {0.0, 0.0, 0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testCXBellState() {
        // CX(H|0⟩ ⊗ |0⟩) = (|00⟩ + |11⟩) / √2 (Bell state)
        double[][] state = allocateBasisState(2, 0); // |00⟩
        double[] real = state[0];
        double[] imag = state[1];

        // First apply H to qubit 0
        SingleQubitKernels.applyHadamard(real, imag, 2, 0);
        // Then apply CX
        TwoQubitKernels.applyCX(real, imag, 2, 0, 1);

        double[] expectedReal = {SQRT_2_INV, 0.0, 0.0, SQRT_2_INV}; // (|00⟩ + |11⟩) / √2
        double[] expectedImag = {0.0, 0.0, 0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testCZControlZero() {
        // CZ|00⟩ = |00⟩ (control=0, no phase)
        double[][] state = allocateBasisState(2, 0); // |00⟩
        double[] real = state[0];
        double[] imag = state[1];

        TwoQubitKernels.applyCZ(real, imag, 2, 0, 1);

        double[] expectedReal = {1.0, 0.0, 0.0, 0.0};
        double[] expectedImag = {0.0, 0.0, 0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testCZBothOne() {
        // CZ|11⟩ = -|11⟩ (both qubits=1, apply phase)
        double[][] state = allocateBasisState(2, 3); // |11⟩
        double[] real = state[0];
        double[] imag = state[1];

        TwoQubitKernels.applyCZ(real, imag, 2, 0, 1);

        double[] expectedReal = {0.0, 0.0, 0.0, -1.0}; // -|11⟩
        double[] expectedImag = {0.0, 0.0, 0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testCZSuperposition() {
        // CZ on superposition: (|00⟩ + |01⟩ + |10⟩ + |11⟩) / 2 → (|00⟩ + |01⟩ + |10⟩ - |11⟩) / 2
        double[] real = {0.5, 0.5, 0.5, 0.5};
        double[] imag = {0.0, 0.0, 0.0, 0.0};

        TwoQubitKernels.applyCZ(real, imag, 2, 0, 1);

        double[] expectedReal = {0.5, 0.5, 0.5, -0.5};
        double[] expectedImag = {0.0, 0.0, 0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testSwapBasicStates() {
        // SWAP|01⟩ = |10⟩
        double[][] state = allocateBasisState(2, 1); // |01⟩
        double[] real = state[0];
        double[] imag = state[1];

        TwoQubitKernels.applySWAP(real, imag, 2, 0, 1);

        double[] expectedReal = {0.0, 0.0, 1.0, 0.0}; // |10⟩
        double[] expectedImag = {0.0, 0.0, 0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testSwapEntangled() {
        // SWAP on Bell state: (|00⟩ + |11⟩) / √2 → (|00⟩ + |11⟩) / √2 (unchanged)
        double[] real = {SQRT_2_INV, 0.0, 0.0, SQRT_2_INV};
        double[] imag = {0.0, 0.0, 0.0, 0.0};

        TwoQubitKernels.applySWAP(real, imag, 2, 0, 1);

        double[] expectedReal = {SQRT_2_INV, 0.0, 0.0, SQRT_2_INV}; // Unchanged
        double[] expectedImag = {0.0, 0.0, 0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testSwapAsymmetricEntangled() {
        // SWAP on (|01⟩ + |10⟩) / √2 → (|10⟩ + |01⟩) / √2 = (|01⟩ + |10⟩) / √2 (unchanged)
        double[] real = {0.0, SQRT_2_INV, SQRT_2_INV, 0.0};
        double[] imag = {0.0, 0.0, 0.0, 0.0};

        TwoQubitKernels.applySWAP(real, imag, 2, 0, 1);

        double[] expectedReal = {0.0, SQRT_2_INV, SQRT_2_INV, 0.0}; // Unchanged
        double[] expectedImag = {0.0, 0.0, 0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testSwapComplexEntangled() {
        // SWAP on (|01⟩ + i|10⟩) / √2 → (|10⟩ + i|01⟩) / √2
        double[] real = {0.0, SQRT_2_INV, 0.0, 0.0};
        double[] imag = {0.0, 0.0, SQRT_2_INV, 0.0};

        TwoQubitKernels.applySWAP(real, imag, 2, 0, 1);

        double[] expectedReal = {0.0, 0.0, SQRT_2_INV, 0.0}; // |10⟩ component
        double[] expectedImag = {0.0, SQRT_2_INV, 0.0, 0.0}; // i|01⟩ component
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    // Multi-qubit tests

    @Test
    void testThreeQubitHadamard() {
        // H on qubit 1 of |000⟩ → (|000⟩ + |010⟩) / √2
        double[][] state = allocateBasisState(3, 0); // |000⟩
        double[] real = state[0];
        double[] imag = state[1];

        SingleQubitKernels.applyHadamard(real, imag, 3, 1);

        double[] expectedReal = {SQRT_2_INV, 0.0, SQRT_2_INV, 0.0, 0.0, 0.0, 0.0, 0.0};
        double[] expectedImag = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }

    @Test
    void testThreeQubitCX() {
        // CX(control=0, target=2) on |101⟩ → |001⟩ (control=1, flip target bit 2)
        double[][] state = allocateBasisState(3, 5); // |101⟩
        double[] real = state[0];
        double[] imag = state[1];

        TwoQubitKernels.applyCX(real, imag, 3, 0, 2);

        double[] expectedReal = {0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}; // |001⟩
        double[] expectedImag = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        assertAmplitudes(real, imag, expectedReal, expectedImag, EPSILON);
    }
}
