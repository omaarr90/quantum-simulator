package com.omaarr90.core.statevector;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import jdk.incubator.vector.DoubleVector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Comprehensive tests for the StateVector class. */
class StateVectorTest {

    private static final double EPSILON = 1e-9;
    private static final int VLEN = DoubleVector.SPECIES_PREFERRED.length();

    /**
     * Test Case 1: Allocation & Padding Given nQubits = 5 (logicalSize = 32), assert paddedSize ≥
     * 32 and paddedSize % VLEN == 0
     */
    @Test
    void testAllocationAndPadding() {
        // Test the specific requirement from the issue description
        int nQubits = 5;
        int expectedLogicalSize = 32;
        StateVector vector = StateVector.allocate(nQubits);

        assertAll(
                "Allocation and Padding for nQubits = 5",
                () ->
                        assertEquals(
                                expectedLogicalSize,
                                vector.logicalSize(),
                                "logicalSize should be 2^5 = 32"),
                () ->
                        assertTrue(
                                vector.paddedSize() >= expectedLogicalSize,
                                "paddedSize should be >= logicalSize"),
                () ->
                        assertEquals(
                                0,
                                vector.paddedSize() % VLEN,
                                "paddedSize should be multiple of VLEN"));
    }

    /**
     * Test Case 2: Ground-State Initialisation Verify amplitude 0 is 1 + 0i and for all k > 0,
     * real[k] == imag[k] == 0
     */
    @Test
    void testGroundStateInitialisation() {
        StateVector vector = StateVector.allocate(3); // 8 amplitudes

        assertAll(
                "Ground state |000⟩ initialization",
                () ->
                        assertEquals(
                                1.0,
                                vector.real()[0],
                                EPSILON,
                                "real[0] should be 1.0 for ground state"),
                () ->
                        assertEquals(
                                0.0,
                                vector.imag()[0],
                                EPSILON,
                                "imag[0] should be 0.0 for ground state"));

        // Verify all other amplitudes are zero
        for (int k = 1; k < vector.logicalSize(); k++) {
            final int index = k; // Make effectively final for lambda
            assertAll(
                    "All other amplitudes should be zero for k = " + k,
                    () ->
                            assertEquals(
                                    0.0,
                                    vector.real()[index],
                                    EPSILON,
                                    "real[" + index + "] should be 0.0"),
                    () ->
                            assertEquals(
                                    0.0,
                                    vector.imag()[index],
                                    EPSILON,
                                    "imag[" + index + "] should be 0.0"));
        }
    }

    /** Test Case 5: Edge Cases allocate(-1) and allocate(31) must throw IllegalArgumentException */
    @Test
    void testEdgeCases() {
        assertAll(
                "Invalid qubit count edge cases",
                () ->
                        assertThrows(
                                IllegalArgumentException.class,
                                () -> StateVector.allocate(-1),
                                "allocate(-1) should throw IllegalArgumentException"),
                () ->
                        assertThrows(
                                IllegalArgumentException.class,
                                () -> StateVector.allocate(31),
                                "allocate(31) should throw IllegalArgumentException"));

        // Test boundary values that should work
        assertAll(
                "Valid boundary cases",
                () ->
                        assertDoesNotThrow(
                                () -> StateVector.allocate(0),
                                "allocate(0) should not throw exception"),
                () ->
                        assertDoesNotThrow(
                                () -> StateVector.allocate(20),
                                "allocate(20) should not throw exception (reasonable upper"
                                        + " bound)"));
    }

    /**
     * Parameterized test to exercise multiple nQubits values (1-10) Tests allocation and padding
     * behavior across different qubit counts
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void testParameterizedAllocation(int nQubits) {
        StateVector vector = StateVector.allocate(nQubits);
        int expectedLogicalSize = 1 << nQubits; // 2^nQubits

        assertAll(
                "Parameterized allocation test for nQubits = " + nQubits,
                () ->
                        assertEquals(
                                expectedLogicalSize,
                                vector.logicalSize(),
                                "logicalSize should be 2^" + nQubits + " = " + expectedLogicalSize),
                () ->
                        assertTrue(
                                vector.paddedSize() >= expectedLogicalSize,
                                "paddedSize should be >= logicalSize"),
                () ->
                        assertEquals(
                                0,
                                vector.paddedSize() % VLEN,
                                "paddedSize should be multiple of VLEN"),
                () ->
                        assertEquals(
                                1.0,
                                vector.real()[0],
                                EPSILON,
                                "real[0] should be 1.0 for ground state"),
                () ->
                        assertEquals(
                                0.0,
                                vector.imag()[0],
                                EPSILON,
                                "imag[0] should be 0.0 for ground state"));

        // Verify all other amplitudes are zero
        for (int k = 1; k < vector.logicalSize(); k++) {
            final int index = k;
            assertEquals(
                    0.0,
                    vector.real()[index],
                    EPSILON,
                    "real[" + index + "] should be 0.0 for nQubits=" + nQubits);
            assertEquals(
                    0.0,
                    vector.imag()[index],
                    EPSILON,
                    "imag[" + index + "] should be 0.0 for nQubits=" + nQubits);
        }
    }

    @Test
    void testPaddingSizeCalculation() {
        // Test that padding is calculated correctly for SIMD alignment
        for (int nQubits = 0; nQubits <= 10; nQubits++) {
            StateVector vector = StateVector.allocate(nQubits);
            int logicalSize = 1 << nQubits;
            int expectedPaddedSize = (logicalSize + VLEN - 1) & ~(VLEN - 1);

            assertEquals(logicalSize, vector.logicalSize());
            assertEquals(expectedPaddedSize, vector.paddedSize());
            assertEquals(0, vector.paddedSize() % VLEN, "Padded size must be multiple of VLEN");
            assertTrue(
                    vector.paddedSize() >= vector.logicalSize(),
                    "Padded size must be >= logical size");
        }
    }

    @Test
    void testPaddingElementsAreZero() {
        // Test that padding elements are initialized to zero
        StateVector vector = StateVector.allocate(3); // logicalSize = 8

        // Check that padding elements (if any) are zero
        for (int i = vector.logicalSize(); i < vector.paddedSize(); i++) {
            assertEquals(0.0, vector.real()[i], EPSILON, "Padding real[" + i + "] should be 0.0");
            assertEquals(0.0, vector.imag()[i], EPSILON, "Padding imag[" + i + "] should be 0.0");
        }
    }

    /**
     * Test Case 3: Index Helper For randomly chosen qubit q and amplitude index idx, confirm idx ^
     * indexOf(q,1) flips bit q
     */
    @Test
    void testIndexHelper() {
        StateVector vector = StateVector.allocate(4); // 4 qubits for testing
        Random random = new Random(42); // Fixed seed for reproducibility

        // Test basic indexOf functionality
        assertAll(
                "Basic indexOf functionality",
                () -> assertEquals(0, vector.indexOf(0, 0), "indexOf(0, 0) should return 0"),
                () -> assertEquals(1, vector.indexOf(0, 1), "indexOf(0, 1) should return 1"),
                () -> assertEquals(2, vector.indexOf(1, 1), "indexOf(1, 1) should return 2"),
                () -> assertEquals(4, vector.indexOf(2, 1), "indexOf(2, 1) should return 4"),
                () -> assertEquals(8, vector.indexOf(3, 1), "indexOf(3, 1) should return 8"));

        // Test bit flipping behavior with random indices
        for (int test = 0; test < 10; test++) {
            int qubit = random.nextInt(4); // Random qubit 0-3
            int idx = random.nextInt(16); // Random amplitude index 0-15

            // Test flipping qubit using indexOf
            int flippedIdx = idx ^ vector.indexOf(qubit, 1);
            boolean originalBit = (idx & (1 << qubit)) != 0;
            boolean flippedBit = (flippedIdx & (1 << qubit)) != 0;

            final int testNum = test;
            final int finalQubit = qubit;
            final int finalIdx = idx;
            final int finalFlippedIdx = flippedIdx;

            assertAll(
                    "Bit flip test " + testNum + " (qubit=" + qubit + ", idx=" + idx + ")",
                    () ->
                            assertNotEquals(
                                    originalBit,
                                    flippedBit,
                                    "Bit "
                                            + finalQubit
                                            + " should be flipped from "
                                            + originalBit
                                            + " to "
                                            + flippedBit),
                    () ->
                            assertEquals(
                                    finalIdx ^ (1 << finalQubit),
                                    finalFlippedIdx,
                                    "XOR should flip exactly bit " + finalQubit),
                    () ->
                            assertEquals(
                                    !originalBit,
                                    flippedBit,
                                    "Flipped bit should be opposite of original bit"));
        }
    }

    @Test
    void testIndexOfInvalidBasisState() {
        StateVector vector = StateVector.allocate(2);

        assertThrows(IllegalArgumentException.class, () -> vector.indexOf(0, -1));
        assertThrows(IllegalArgumentException.class, () -> vector.indexOf(0, 2));

        // Valid basis states should work
        assertDoesNotThrow(() -> vector.indexOf(0, 0));
        assertDoesNotThrow(() -> vector.indexOf(0, 1));
    }

    @Test
    void testArrayAccessors() {
        StateVector vector = StateVector.allocate(2);

        // Test that we get the actual arrays (not copies)
        double[] realArray = vector.real();
        double[] imagArray = vector.imag();

        assertNotNull(realArray);
        assertNotNull(imagArray);
        assertEquals(vector.paddedSize(), realArray.length);
        assertEquals(vector.paddedSize(), imagArray.length);

        // Modify through accessor and verify change is reflected
        realArray[1] = 0.5;
        imagArray[1] = 0.3;
        assertEquals(0.5, vector.real()[1], EPSILON);
        assertEquals(0.3, vector.imag()[1], EPSILON);
    }

    /**
     * Test Case 4: Clone Independence Mutate cloned.real()[0] and ensure original vector remains
     * untouched
     */
    @Test
    void testCloneIndependence() {
        StateVector original = StateVector.allocate(3);

        // Modify the original to have some non-zero values
        original.real()[1] = 0.7;
        original.imag()[1] = 0.2;
        original.real()[2] = 0.3;
        original.imag()[2] = 0.4;

        StateVector cloned = original.clone();

        // Store original values for comparison
        double originalReal0 = original.real()[0];
        double originalImag0 = original.imag()[0];
        double originalReal1 = original.real()[1];
        double originalImag1 = original.imag()[1];

        // Mutate cloned vector
        cloned.real()[0] = 0.9;
        cloned.imag()[0] = 0.1;
        cloned.real()[1] = 0.5;

        assertAll(
                "Clone independence verification",
                () ->
                        assertNotSame(
                                original.real(),
                                cloned.real(),
                                "Cloned real array should be different instance"),
                () ->
                        assertNotSame(
                                original.imag(),
                                cloned.imag(),
                                "Cloned imag array should be different instance"),
                () ->
                        assertEquals(
                                originalReal0,
                                original.real()[0],
                                EPSILON,
                                "Original real[0] should remain unchanged after cloned mutation"),
                () ->
                        assertEquals(
                                originalImag0,
                                original.imag()[0],
                                EPSILON,
                                "Original imag[0] should remain unchanged after cloned mutation"),
                () ->
                        assertEquals(
                                originalReal1,
                                original.real()[1],
                                EPSILON,
                                "Original real[1] should remain unchanged after cloned mutation"),
                () ->
                        assertEquals(
                                originalImag1,
                                original.imag()[1],
                                EPSILON,
                                "Original imag[1] should remain unchanged after cloned mutation"),
                () ->
                        assertEquals(
                                0.9,
                                cloned.real()[0],
                                EPSILON,
                                "Cloned real[0] should have new value"),
                () ->
                        assertEquals(
                                0.1,
                                cloned.imag()[0],
                                EPSILON,
                                "Cloned imag[0] should have new value"));
    }

    @Test
    void testCloneIncludesPadding() {
        StateVector original = StateVector.allocate(3);

        // Modify padding area (if it exists)
        if (original.paddedSize() > original.logicalSize()) {
            original.real()[original.logicalSize()] = 999.0; // This should remain 0 in practice
            original.imag()[original.logicalSize()] = 888.0; // but we test the copy mechanism
        }

        StateVector cloned = original.clone();

        // Verify entire arrays are copied including padding
        assertEquals(original.paddedSize(), cloned.paddedSize());
        for (int i = 0; i < original.paddedSize(); i++) {
            assertEquals(
                    original.real()[i], cloned.real()[i], EPSILON, "real[" + i + "] should match");
            assertEquals(
                    original.imag()[i], cloned.imag()[i], EPSILON, "imag[" + i + "] should match");
        }
    }

    @Test
    void testToStringBasicFormat() {
        StateVector vector = StateVector.allocate(2);
        String str = vector.toString();

        // Should contain basic information
        assertTrue(str.contains("StateVector"));
        assertTrue(str.contains("2 qubits"));
        assertTrue(str.contains("4 amplitudes"));

        // Should show the first amplitude
        assertTrue(str.contains("[0]:"));
    }

    @Test
    void testToStringWithNonZeroAmplitudes() {
        StateVector vector = StateVector.allocate(2);

        // Set some non-zero amplitudes
        vector.real()[1] = 0.6;
        vector.imag()[1] = 0.8;

        String str = vector.toString();

        // Should show both zero and non-zero amplitudes
        assertTrue(str.contains("[0]:"));
        assertTrue(str.contains("[1]:"));
    }

    @Test
    void testMemoryLayoutSoA() {
        StateVector vector = StateVector.allocate(4);

        // Verify Structure-of-Arrays layout
        assertNotNull(vector.real());
        assertNotNull(vector.imag());
        assertEquals(vector.real().length, vector.imag().length);

        // Arrays should be separate instances
        assertNotSame(vector.real(), vector.imag());

        // Test that we can access all logical elements
        for (int i = 0; i < vector.logicalSize(); i++) {
            final int index = i; // Make effectively final for lambda
            // Should not throw IndexOutOfBoundsException
            assertDoesNotThrow(
                    () -> {
                        double r = vector.real()[index];
                        double im = vector.imag()[index];
                    });
        }
    }

    @Test
    void testLargeStateVector() {
        // Test with a reasonably large state vector
        StateVector vector = StateVector.allocate(20); // 2^20 = ~1M amplitudes

        assertEquals(1 << 20, vector.logicalSize());
        assertEquals(1.0, vector.real()[0], EPSILON);
        assertEquals(0.0, vector.imag()[0], EPSILON);

        // Verify padding calculation for large vectors
        assertTrue(vector.paddedSize() >= vector.logicalSize());
        assertEquals(0, vector.paddedSize() % VLEN);
    }

    @Test
    void testZeroQubitEdgeCase() {
        StateVector vector = StateVector.allocate(0);

        assertEquals(1, vector.logicalSize());
        assertEquals(1.0, vector.real()[0], EPSILON);
        assertEquals(0.0, vector.imag()[0], EPSILON);

        // Test indexOf with 0 qubits (edge case)
        assertEquals(0, vector.indexOf(0, 0));
        assertEquals(1, vector.indexOf(0, 1));
    }
}
