package com.omaarr90.qsim.statevector.parallel;

import static org.junit.jupiter.api.Assertions.*;

import com.omaarr90.core.statevector.StateVector;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for ParallelSweep utility class.
 *
 * <p>Tests verify correct chunking logic, slice creation, parallel execution coordination, and
 * fallback behavior for small circuits.
 */
class ParallelSweepTest {

    @Test
    void testDecideSlices_SmallCircuit() {
        // Circuits with ≤12 qubits should use serial execution
        assertEquals(1, ParallelSweep.decideSlices(1024, 10)); // 2^10 = 1024
        assertEquals(1, ParallelSweep.decideSlices(4096, 12)); // 2^12 = 4096
        assertEquals(1, ParallelSweep.decideSlices(8, 3)); // 2^3 = 8
    }

    @Test
    void testDecideSlices_LargeCircuit() {
        // Circuits with >12 qubits should use parallel execution
        int slices13 = ParallelSweep.decideSlices(8192, 13); // 2^13 = 8192
        int slices15 = ParallelSweep.decideSlices(32768, 15); // 2^15 = 32768
        int slices20 = ParallelSweep.decideSlices(1048576, 20); // 2^20 = 1048576

        assertTrue(slices13 > 1, "13-qubit circuit should use parallel execution");
        assertTrue(slices15 > 1, "15-qubit circuit should use parallel execution");
        assertTrue(slices20 > 1, "20-qubit circuit should use parallel execution");

        // Slices should be power of 2
        assertTrue(Integer.bitCount(slices13) == 1, "Slice count should be power of 2");
        assertTrue(Integer.bitCount(slices15) == 1, "Slice count should be power of 2");
        assertTrue(Integer.bitCount(slices20) == 1, "Slice count should be power of 2");
    }

    @Test
    void testDecideSlices_ForceSerial() {
        // Test system property override
        System.setProperty("qsim.forceSerial", "true");
        try {
            assertEquals(1, ParallelSweep.decideSlices(32768, 15));
            assertEquals(1, ParallelSweep.decideSlices(1048576, 20));
        } finally {
            System.clearProperty("qsim.forceSerial");
        }
    }

    @Test
    void testDecideSlices_MinAmplitudesPerSlice() {
        // Very small problems should not be over-parallelized
        int slices = ParallelSweep.decideSlices(2048, 13); // 2^11 = 2048, just above threshold

        // Should not create more slices than can maintain minimum amplitudes per slice
        assertTrue(2048 / slices >= 1024, "Should maintain minimum amplitudes per slice");
    }

    @Test
    void testSlice_SingleSlice() {
        List<AmplitudeSlice> slices = ParallelSweep.slice(1000, 1);

        assertEquals(1, slices.size());
        assertEquals(new AmplitudeSlice(0, 1000), slices.get(0));
    }

    @Test
    void testSlice_EvenDivision() {
        List<AmplitudeSlice> slices = ParallelSweep.slice(1000, 4);

        assertEquals(4, slices.size());

        // Note: SIMD alignment may adjust slice boundaries, so we test properties rather than exact
        // values
        // Verify complete coverage
        assertEquals(0, slices.get(0).start());
        assertEquals(1000, slices.get(slices.size() - 1).end());

        // Verify no gaps or overlaps
        for (int i = 1; i < slices.size(); i++) {
            assertEquals(slices.get(i - 1).end(), slices.get(i).start());
        }

        // Verify total coverage
        int totalCovered = slices.stream().mapToInt(AmplitudeSlice::length).sum();
        assertEquals(1000, totalCovered, "Slices should cover entire space");
    }

    @Test
    void testSlice_UnevenDivision() {
        List<AmplitudeSlice> slices = ParallelSweep.slice(1000, 3);

        assertEquals(3, slices.size());

        // Note: SIMD alignment may adjust slice boundaries, so we test properties rather than exact
        // values
        // Verify complete coverage
        assertEquals(0, slices.get(0).start());
        assertEquals(1000, slices.get(slices.size() - 1).end());

        // Verify no gaps or overlaps
        for (int i = 1; i < slices.size(); i++) {
            assertEquals(slices.get(i - 1).end(), slices.get(i).start());
        }

        // Verify total coverage
        int totalCovered = slices.stream().mapToInt(AmplitudeSlice::length).sum();
        assertEquals(1000, totalCovered, "Slices should cover entire space");
    }

    @Test
    void testSlice_PowerOfTwoAmplitudes() {
        // Test with typical quantum state sizes
        List<AmplitudeSlice> slices = ParallelSweep.slice(8192, 8); // 2^13 amplitudes, 8 slices

        assertEquals(8, slices.size());
        assertEquals(1024, slices.get(0).length()); // Each slice has 1024 amplitudes

        // Verify all slices have the same size for power-of-2 division
        for (AmplitudeSlice slice : slices) {
            assertEquals(1024, slice.length());
        }
    }

    @Test
    void testSlice_InvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> ParallelSweep.slice(1000, 0));

        assertThrows(IllegalArgumentException.class, () -> ParallelSweep.slice(1000, -1));

        assertThrows(
                IllegalArgumentException.class,
                () -> ParallelSweep.slice(4, 8)); // More slices than amplitudes
    }

    @Test
    void testForEachSlice_SerialExecution() throws InterruptedException {
        StateVector stateVector = StateVector.allocate(10); // Small circuit, should be serial
        AtomicInteger callCount = new AtomicInteger(0);
        AtomicInteger totalElements = new AtomicInteger(0);

        ParallelSweep.forEachSlice(
                stateVector,
                10,
                slice -> {
                    callCount.incrementAndGet();
                    totalElements.addAndGet(slice.length());
                });

        assertEquals(1, callCount.get(), "Should use serial execution for small circuit");
        assertEquals(1024, totalElements.get(), "Should process all 2^10 amplitudes");
    }

    @Test
    void testForEachSlice_ParallelExecution() throws InterruptedException {
        StateVector stateVector = StateVector.allocate(15); // Large circuit, should be parallel
        AtomicInteger callCount = new AtomicInteger(0);
        AtomicInteger totalElements = new AtomicInteger(0);

        ParallelSweep.forEachSlice(
                stateVector,
                15,
                slice -> {
                    callCount.incrementAndGet();
                    totalElements.addAndGet(slice.length());

                    // Simulate some work to test parallel execution
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

        assertTrue(callCount.get() > 1, "Should use parallel execution for large circuit");
        assertEquals(32768, totalElements.get(), "Should process all 2^15 amplitudes");
    }

    @Test
    void testForEachSlice_ExceptionHandling() {
        StateVector stateVector = StateVector.allocate(15);

        RuntimeException testException = new RuntimeException("Test exception");

        RuntimeException thrown =
                assertThrows(
                        RuntimeException.class,
                        () -> {
                            ParallelSweep.forEachSlice(
                                    stateVector,
                                    15,
                                    slice -> {
                                        if (slice.start() == 0) {
                                            throw testException;
                                        }
                                    });
                        });

        assertEquals(testException, thrown);
    }

    @Test
    void testForEachSlice_InterruptionHandling() {
        StateVector stateVector = StateVector.allocate(15);

        // The forEachSlice method catches InterruptedException and wraps it in RuntimeException
        RuntimeException thrown =
                assertThrows(
                        RuntimeException.class,
                        () -> {
                            ParallelSweep.forEachSlice(
                                    stateVector,
                                    15,
                                    slice -> {
                                        Thread.currentThread().interrupt();
                                        try {
                                            Thread.sleep(
                                                    100); // This should throw InterruptedException
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                        });

        // Verify the cause is an InterruptedException
        assertTrue(thrown.getCause() instanceof InterruptedException);
    }

    @Test
    void testDescribeSlicing() {
        String description = ParallelSweep.describeSlicing(1024, 10);

        assertTrue(description.contains("1024 amplitudes"));
        assertTrue(description.contains("10 qubits"));
        assertTrue(description.contains("1 slices")); // Should be serial for 10 qubits
        assertTrue(description.contains("Slice 0: [0, 1024)"));
    }

    @Test
    void testDescribeSlicing_Parallel() {
        String description = ParallelSweep.describeSlicing(32768, 15);

        assertTrue(description.contains("32768 amplitudes"));
        assertTrue(description.contains("15 qubits"));
        assertFalse(description.contains("1 slices")); // Should be parallel for 15 qubits

        // Should contain multiple slice descriptions
        assertTrue(description.contains("Slice 0:"));
        assertTrue(description.contains("Slice 1:"));
    }

    @Test
    void testSliceAlignment() {
        // Test that slices are reasonably aligned for SIMD operations
        List<AmplitudeSlice> slices = ParallelSweep.slice(8192, 4);

        for (AmplitudeSlice slice : slices) {
            // Each slice should have a reasonable size for vectorization
            assertTrue(slice.length() >= 8, "Slice should be large enough for SIMD operations");
        }
    }

    @Test
    void testSliceCoverage() {
        // Test that slices cover the entire amplitude space without gaps or overlaps
        int[] testSizes = {1000, 1024, 2048, 4096, 8192};
        int[] testSliceCounts = {1, 2, 4, 8};

        for (int size : testSizes) {
            for (int sliceCount : testSliceCounts) {
                if (size >= sliceCount) { // Valid combination
                    List<AmplitudeSlice> slices = ParallelSweep.slice(size, sliceCount);

                    assertEquals(sliceCount, slices.size());

                    // Check coverage
                    assertEquals(0, slices.get(0).start(), "First slice should start at 0");
                    assertEquals(
                            size,
                            slices.get(slices.size() - 1).end(),
                            "Last slice should end at total size");

                    // Check no gaps or overlaps
                    for (int i = 1; i < slices.size(); i++) {
                        assertEquals(
                                slices.get(i - 1).end(),
                                slices.get(i).start(),
                                "Slices should be contiguous");
                    }

                    // Check total coverage
                    int totalCovered = slices.stream().mapToInt(AmplitudeSlice::length).sum();
                    assertEquals(size, totalCovered, "Slices should cover entire space");
                }
            }
        }
    }
}
