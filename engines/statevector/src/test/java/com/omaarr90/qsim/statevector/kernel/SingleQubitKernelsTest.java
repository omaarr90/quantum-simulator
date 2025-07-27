package com.omaarr90.qsim.statevector.kernel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for SingleQubitKernels gate operations.
 *
 * <p>Tests verify correct quantum transformations for all single-qubit gates using known input
 * states and expected output states.
 */
class SingleQubitKernelsTest {

    private static final double EPSILON = 1e-12;
    private static final double SQRT_2_INV = 1.0 / Math.sqrt(2.0);

    @Test
    void testHadamardOnSingleQubit() {
        // Test H|0⟩ = (|0⟩ + |1⟩) / √2
        double[] real = {1.0, 0.0}; // |0⟩ state
        double[] imag = {0.0, 0.0};

        SingleQubitKernels.applyHadamard(real, imag, 1, 0);

        assertEquals(SQRT_2_INV, real[0], EPSILON, "H|0⟩ real[0] should be 1/√2");
        assertEquals(0.0, imag[0], EPSILON, "H|0⟩ imag[0] should be 0");
        assertEquals(SQRT_2_INV, real[1], EPSILON, "H|0⟩ real[1] should be 1/√2");
        assertEquals(0.0, imag[1], EPSILON, "H|0⟩ imag[1] should be 0");
    }

    @Test
    void testHadamardOnOneState() {
        // Test H|1⟩ = (|0⟩ - |1⟩) / √2
        double[] real = {0.0, 1.0}; // |1⟩ state
        double[] imag = {0.0, 0.0};

        SingleQubitKernels.applyHadamard(real, imag, 1, 0);

        assertEquals(SQRT_2_INV, real[0], EPSILON, "H|1⟩ real[0] should be 1/√2");
        assertEquals(0.0, imag[0], EPSILON, "H|1⟩ imag[0] should be 0");
        assertEquals(-SQRT_2_INV, real[1], EPSILON, "H|1⟩ real[1] should be -1/√2");
        assertEquals(0.0, imag[1], EPSILON, "H|1⟩ imag[1] should be 0");
    }

    @Test
    void testHadamardOnTwoQubits() {
        // Test H on qubit 0 of |00⟩ state
        double[] real = {1.0, 0.0, 0.0, 0.0}; // |00⟩ state
        double[] imag = {0.0, 0.0, 0.0, 0.0};

        SingleQubitKernels.applyHadamard(real, imag, 2, 0);

        // Should result in (|00⟩ + |01⟩) / √2
        assertEquals(SQRT_2_INV, real[0], EPSILON, "H|00⟩ real[0] should be 1/√2");
        assertEquals(SQRT_2_INV, real[1], EPSILON, "H|00⟩ real[1] should be 1/√2");
        assertEquals(0.0, real[2], EPSILON, "H|00⟩ real[2] should be 0");
        assertEquals(0.0, real[3], EPSILON, "H|00⟩ real[3] should be 0");
    }

    @Test
    void testPauliXOnSingleQubit() {
        // Test X|0⟩ = |1⟩
        double[] real = {1.0, 0.0}; // |0⟩ state
        double[] imag = {0.0, 0.0};

        SingleQubitKernels.applyPauliX(real, imag, 1, 0);

        assertEquals(0.0, real[0], EPSILON, "X|0⟩ real[0] should be 0");
        assertEquals(0.0, imag[0], EPSILON, "X|0⟩ imag[0] should be 0");
        assertEquals(1.0, real[1], EPSILON, "X|0⟩ real[1] should be 1");
        assertEquals(0.0, imag[1], EPSILON, "X|0⟩ imag[1] should be 0");
    }

    @Test
    void testPauliXOnOneState() {
        // Test X|1⟩ = |0⟩
        double[] real = {0.0, 1.0}; // |1⟩ state
        double[] imag = {0.0, 0.0};

        SingleQubitKernels.applyPauliX(real, imag, 1, 0);

        assertEquals(1.0, real[0], EPSILON, "X|1⟩ real[0] should be 1");
        assertEquals(0.0, imag[0], EPSILON, "X|1⟩ imag[0] should be 0");
        assertEquals(0.0, real[1], EPSILON, "X|1⟩ real[1] should be 0");
        assertEquals(0.0, imag[1], EPSILON, "X|1⟩ imag[1] should be 0");
    }

    @Test
    void testPauliYOnSingleQubit() {
        // Test Y|0⟩ = i|1⟩
        double[] real = {1.0, 0.0}; // |0⟩ state
        double[] imag = {0.0, 0.0};

        SingleQubitKernels.applyPauliY(real, imag, 1, 0);

        assertEquals(0.0, real[0], EPSILON, "Y|0⟩ real[0] should be 0");
        assertEquals(0.0, imag[0], EPSILON, "Y|0⟩ imag[0] should be 0");
        assertEquals(0.0, real[1], EPSILON, "Y|0⟩ real[1] should be 0");
        assertEquals(1.0, imag[1], EPSILON, "Y|0⟩ imag[1] should be 1");
    }

    @Test
    void testPauliYOnOneState() {
        // Test Y|1⟩ = -i|0⟩
        double[] real = {0.0, 1.0}; // |1⟩ state
        double[] imag = {0.0, 0.0};

        SingleQubitKernels.applyPauliY(real, imag, 1, 0);

        assertEquals(0.0, real[0], EPSILON, "Y|1⟩ real[0] should be 0");
        assertEquals(-1.0, imag[0], EPSILON, "Y|1⟩ imag[0] should be -1");
        assertEquals(0.0, real[1], EPSILON, "Y|1⟩ real[1] should be 0");
        assertEquals(0.0, imag[1], EPSILON, "Y|1⟩ imag[1] should be 0");
    }

    @Test
    void testPauliZOnSingleQubit() {
        // Test Z|0⟩ = |0⟩
        double[] real = {1.0, 0.0}; // |0⟩ state
        double[] imag = {0.0, 0.0};

        SingleQubitKernels.applyPauliZ(real, imag, 1, 0);

        assertEquals(1.0, real[0], EPSILON, "Z|0⟩ real[0] should be 1");
        assertEquals(0.0, imag[0], EPSILON, "Z|0⟩ imag[0] should be 0");
        assertEquals(0.0, real[1], EPSILON, "Z|0⟩ real[1] should be 0");
        assertEquals(0.0, imag[1], EPSILON, "Z|0⟩ imag[1] should be 0");
    }

    @Test
    void testPauliZOnOneState() {
        // Test Z|1⟩ = -|1⟩
        double[] real = {0.0, 1.0}; // |1⟩ state
        double[] imag = {0.0, 0.0};

        SingleQubitKernels.applyPauliZ(real, imag, 1, 0);

        assertEquals(0.0, real[0], EPSILON, "Z|1⟩ real[0] should be 0");
        assertEquals(0.0, imag[0], EPSILON, "Z|1⟩ imag[0] should be 0");
        assertEquals(-1.0, real[1], EPSILON, "Z|1⟩ real[1] should be -1");
        assertEquals(0.0, imag[1], EPSILON, "Z|1⟩ imag[1] should be 0");
    }

    @Test
    void testRXRotationPiOnSingleQubit() {
        // Test RX(π)|0⟩ = -i|1⟩
        double[] real = {1.0, 0.0}; // |0⟩ state
        double[] imag = {0.0, 0.0};

        SingleQubitKernels.applyRX(real, imag, 1, 0, Math.PI);

        assertEquals(0.0, real[0], EPSILON, "RX(π)|0⟩ real[0] should be 0");
        assertEquals(0.0, imag[0], EPSILON, "RX(π)|0⟩ imag[0] should be 0");
        assertEquals(0.0, real[1], EPSILON, "RX(π)|0⟩ real[1] should be 0");
        assertEquals(-1.0, imag[1], EPSILON, "RX(π)|0⟩ imag[1] should be -1");
    }

    @Test
    void testRXRotationPiHalfOnSingleQubit() {
        // Test RX(π/2)|0⟩ = (|0⟩ - i|1⟩) / √2
        double[] real = {1.0, 0.0}; // |0⟩ state
        double[] imag = {0.0, 0.0};

        SingleQubitKernels.applyRX(real, imag, 1, 0, Math.PI / 2);

        assertEquals(SQRT_2_INV, real[0], EPSILON, "RX(π/2)|0⟩ real[0] should be 1/√2");
        assertEquals(0.0, imag[0], EPSILON, "RX(π/2)|0⟩ imag[0] should be 0");
        assertEquals(0.0, real[1], EPSILON, "RX(π/2)|0⟩ real[1] should be 0");
        assertEquals(-SQRT_2_INV, imag[1], EPSILON, "RX(π/2)|0⟩ imag[1] should be -1/√2");
    }

    @Test
    void testRYRotationPiOnSingleQubit() {
        // Test RY(π)|0⟩ = |1⟩
        double[] real = {1.0, 0.0}; // |0⟩ state
        double[] imag = {0.0, 0.0};

        SingleQubitKernels.applyRY(real, imag, 1, 0, Math.PI);

        assertEquals(0.0, real[0], EPSILON, "RY(π)|0⟩ real[0] should be 0");
        assertEquals(0.0, imag[0], EPSILON, "RY(π)|0⟩ imag[0] should be 0");
        assertEquals(1.0, real[1], EPSILON, "RY(π)|0⟩ real[1] should be 1");
        assertEquals(0.0, imag[1], EPSILON, "RY(π)|0⟩ imag[1] should be 0");
    }

    @Test
    void testRYRotationPiHalfOnSingleQubit() {
        // Test RY(π/2)|0⟩ = (|0⟩ + |1⟩) / √2
        double[] real = {1.0, 0.0}; // |0⟩ state
        double[] imag = {0.0, 0.0};

        SingleQubitKernels.applyRY(real, imag, 1, 0, Math.PI / 2);

        assertEquals(SQRT_2_INV, real[0], EPSILON, "RY(π/2)|0⟩ real[0] should be 1/√2");
        assertEquals(0.0, imag[0], EPSILON, "RY(π/2)|0⟩ imag[0] should be 0");
        assertEquals(SQRT_2_INV, real[1], EPSILON, "RY(π/2)|0⟩ real[1] should be 1/√2");
        assertEquals(0.0, imag[1], EPSILON, "RY(π/2)|0⟩ imag[1] should be 0");
    }

    @Test
    void testRZRotationPiOnSingleQubit() {
        // Test RZ(π)|0⟩ = -i|0⟩
        double[] real = {1.0, 0.0}; // |0⟩ state
        double[] imag = {0.0, 0.0};

        SingleQubitKernels.applyRZ(real, imag, 1, 0, Math.PI);

        assertEquals(0.0, real[0], EPSILON, "RZ(π)|0⟩ real[0] should be 0");
        assertEquals(-1.0, imag[0], EPSILON, "RZ(π)|0⟩ imag[0] should be -1");
        assertEquals(0.0, real[1], EPSILON, "RZ(π)|0⟩ real[1] should be 0");
        assertEquals(0.0, imag[1], EPSILON, "RZ(π)|0⟩ imag[1] should be 0");
    }

    @Test
    void testRZRotationPiOnOneState() {
        // Test RZ(π)|1⟩ = i|1⟩
        double[] real = {0.0, 1.0}; // |1⟩ state
        double[] imag = {0.0, 0.0};

        SingleQubitKernels.applyRZ(real, imag, 1, 0, Math.PI);

        assertEquals(0.0, real[0], EPSILON, "RZ(π)|1⟩ real[0] should be 0");
        assertEquals(0.0, imag[0], EPSILON, "RZ(π)|1⟩ imag[0] should be 0");
        assertEquals(0.0, real[1], EPSILON, "RZ(π)|1⟩ real[1] should be 0");
        assertEquals(1.0, imag[1], EPSILON, "RZ(π)|1⟩ imag[1] should be 1");
    }

    @Test
    void testRZRotationPiHalfOnSingleQubit() {
        // Test RZ(π/2)|0⟩ = e^(-iπ/4)|0⟩
        double[] real = {1.0, 0.0}; // |0⟩ state
        double[] imag = {0.0, 0.0};

        SingleQubitKernels.applyRZ(real, imag, 1, 0, Math.PI / 2);

        double expectedReal = Math.cos(-Math.PI / 4);
        double expectedImag = Math.sin(-Math.PI / 4);

        assertEquals(expectedReal, real[0], EPSILON, "RZ(π/2)|0⟩ real[0] should be cos(-π/4)");
        assertEquals(expectedImag, imag[0], EPSILON, "RZ(π/2)|0⟩ imag[0] should be sin(-π/4)");
        assertEquals(0.0, real[1], EPSILON, "RZ(π/2)|0⟩ real[1] should be 0");
        assertEquals(0.0, imag[1], EPSILON, "RZ(π/2)|0⟩ imag[1] should be 0");
    }

    @Test
    void testMultiQubitHadamardOnSecondQubit() {
        // Test H on qubit 1 of |00⟩ state (should affect qubits 2,3 in 4-state system)
        double[] real = {1.0, 0.0, 0.0, 0.0}; // |00⟩ state
        double[] imag = {0.0, 0.0, 0.0, 0.0};

        SingleQubitKernels.applyHadamard(real, imag, 2, 1);

        // Should result in (|00⟩ + |10⟩) / √2
        assertEquals(SQRT_2_INV, real[0], EPSILON, "H on qubit 1: real[0] should be 1/√2");
        assertEquals(0.0, real[1], EPSILON, "H on qubit 1: real[1] should be 0");
        assertEquals(SQRT_2_INV, real[2], EPSILON, "H on qubit 1: real[2] should be 1/√2");
        assertEquals(0.0, real[3], EPSILON, "H on qubit 1: real[3] should be 0");
    }

    @Test
    void testSuperpositionState() {
        // Test gates on superposition state (|0⟩ + |1⟩) / √2
        double[] real = {SQRT_2_INV, SQRT_2_INV};
        double[] imag = {0.0, 0.0};

        // Apply Pauli-Z (should negate |1⟩ component)
        SingleQubitKernels.applyPauliZ(real, imag, 1, 0);

        assertEquals(SQRT_2_INV, real[0], EPSILON, "Z on superposition: real[0] should be 1/√2");
        assertEquals(0.0, imag[0], EPSILON, "Z on superposition: imag[0] should be 0");
        assertEquals(-SQRT_2_INV, real[1], EPSILON, "Z on superposition: real[1] should be -1/√2");
        assertEquals(0.0, imag[1], EPSILON, "Z on superposition: imag[1] should be 0");
    }

    @Test
    void testInputValidation() {
        double[] real = {1.0, 0.0};
        double[] imag = {0.0, 0.0};

        // Test null arrays
        assertThrows(
                IllegalArgumentException.class,
                () -> SingleQubitKernels.applyHadamard(null, imag, 1, 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> SingleQubitKernels.applyHadamard(real, null, 1, 0));

        // Test mismatched array lengths
        double[] shortImag = {0.0};
        assertThrows(
                IllegalArgumentException.class,
                () -> SingleQubitKernels.applyHadamard(real, shortImag, 1, 0));

        // Test invalid qubit count
        assertThrows(
                IllegalArgumentException.class,
                () -> SingleQubitKernels.applyHadamard(real, imag, 0, 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> SingleQubitKernels.applyHadamard(real, imag, 31, 0));

        // Test invalid target qubit
        assertThrows(
                IllegalArgumentException.class,
                () -> SingleQubitKernels.applyHadamard(real, imag, 1, -1));
        assertThrows(
                IllegalArgumentException.class,
                () -> SingleQubitKernels.applyHadamard(real, imag, 1, 1));

        // Test array too small for qubit count
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        SingleQubitKernels.applyHadamard(
                                real, imag, 2, 0)); // Need 4 elements for 2 qubits
    }

    @Test
    void testNormalizationPreservation() {
        // Test that gates preserve normalization
        double[] real = {0.6, 0.8}; // Normalized state
        double[] imag = {0.0, 0.0};

        // Apply Hadamard
        SingleQubitKernels.applyHadamard(real, imag, 1, 0);

        // Check normalization is preserved
        double norm2 =
                real[0] * real[0] + imag[0] * imag[0] + real[1] * real[1] + imag[1] * imag[1];
        assertEquals(1.0, norm2, EPSILON, "Normalization should be preserved");
    }

    @Test
    void testComplexState() {
        // Test gates on complex state
        double[] real = {0.5, 0.5};
        double[] imag = {0.5, -0.5};

        // Apply Pauli-Y
        SingleQubitKernels.applyPauliY(real, imag, 1, 0);

        // Y|0⟩ = i|1⟩, Y|1⟩ = -i|0⟩
        // Original: 0.5(1+i)|0⟩ + 0.5(1-i)|1⟩
        // After Y: 0.5(1+i)i|1⟩ + 0.5(1-i)(-i)|0⟩
        //        = 0.5(i-1)|1⟩ + 0.5(-i+i²)|0⟩
        //        = 0.5(i-1)|1⟩ + 0.5(-i-1)|0⟩
        //        = 0.5(-1-i)|0⟩ + 0.5(-1+i)|1⟩

        assertEquals(-0.5, real[0], EPSILON, "Complex Y: real[0]");
        assertEquals(-0.5, imag[0], EPSILON, "Complex Y: imag[0]");
        assertEquals(-0.5, real[1], EPSILON, "Complex Y: real[1]");
        assertEquals(0.5, imag[1], EPSILON, "Complex Y: imag[1]");
    }
}
