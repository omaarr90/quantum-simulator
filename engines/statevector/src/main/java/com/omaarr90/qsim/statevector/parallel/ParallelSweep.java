package com.omaarr90.qsim.statevector.parallel;

import com.omaarr90.core.statevector.StateVector;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Consumer;

/**
 * Utility class for parallel execution of gate kernels across amplitude slices.
 *
 * <p>This class provides the core infrastructure for parallelizing quantum gate operations using
 * Java 21's StructuredTaskScope. It handles chunking of the amplitude space into cache-aligned
 * slices and coordinates parallel execution across multiple threads.
 *
 * <p><strong>PERFORMANCE DESIGN:</strong>
 *
 * <ul>
 *   <li>Slice boundaries are aligned to avoid false sharing between threads
 *   <li>Falls back to serial execution for small circuits (≤12 qubits)
 *   <li>Maintains vectorization within each slice using existing kernel implementations
 *   <li>Uses power-of-two slice counts capped by available CPU cores
 * </ul>
 *
 * <p><strong>THREAD SAFETY:</strong> All methods are thread-safe. The parallel execution operates
 * on disjoint memory regions, requiring no synchronization between worker threads.
 */
public final class ParallelSweep {

    /** Minimum number of qubits required to enable parallel execution. */
    private static final int MIN_QUBITS_FOR_PARALLEL = 13;

    /** Minimum amplitudes per slice to justify parallel overhead. */
    private static final int MIN_AMPLITUDES_PER_SLICE = 1024;

    /** SIMD alignment boundary for slice boundaries (64 bytes = 8 doubles). */
    private static final int SIMD_ALIGNMENT = 8;

    /** System property to force serial execution for testing/debugging. */
    private static final String FORCE_SERIAL_PROPERTY = "qsim.forceSerial";

    // Private constructor - utility class
    private ParallelSweep() {}

    /**
     * Determines the optimal number of slices for parallel execution.
     *
     * <p>The slice count is determined by:
     *
     * <ul>
     *   <li>Available CPU cores (Runtime.getRuntime().availableProcessors())
     *   <li>Rounded down to the nearest power of two
     *   <li>Capped to ensure minimum amplitudes per slice
     *   <li>Returns 1 for circuits with ≤12 qubits (serial fallback)
     * </ul>
     *
     * @param nAmps the total number of amplitudes (2^nQubits)
     * @param nQubits the number of qubits in the circuit
     * @return the number of slices to use (≥1)
     */
    public static int decideSlices(int nAmps, int nQubits) {
        // Force serial execution for small circuits or system property
        if (nQubits <= 12 || Boolean.getBoolean(FORCE_SERIAL_PROPERTY)) {
            return 1;
        }

        int availableCores = Runtime.getRuntime().availableProcessors();

        // Start with available cores, round down to nearest power of 2
        int slices = Integer.highestOneBit(availableCores);

        // Ensure we don't create too many slices for the problem size
        while (slices > 1 && nAmps / slices < MIN_AMPLITUDES_PER_SLICE) {
            slices /= 2;
        }

        // Cap at the number of qubits (can't have more slices than qubits)
        slices = Math.min(slices, nQubits);

        return Math.max(1, slices);
    }

    /**
     * Creates a list of amplitude slices for parallel processing.
     *
     * <p>Slices are created with the following properties:
     *
     * <ul>
     *   <li>Each slice covers a contiguous range of amplitude indices
     *   <li>Slice boundaries are aligned to SIMD boundaries where possible
     *   <li>The last slice may be slightly larger to handle remainder amplitudes
     *   <li>All slices are non-empty and non-overlapping
     * </ul>
     *
     * @param nAmps the total number of amplitudes
     * @param slices the number of slices to create (must be ≥1)
     * @return a list of AmplitudeSlice objects covering [0, nAmps)
     * @throws IllegalArgumentException if slices < 1 or nAmps < slices
     */
    public static List<AmplitudeSlice> slice(int nAmps, int slices) {
        if (slices < 1) {
            throw new IllegalArgumentException("Number of slices must be at least 1: " + slices);
        }
        if (nAmps < slices) {
            throw new IllegalArgumentException(
                    "Cannot create more slices than amplitudes: nAmps="
                            + nAmps
                            + ", slices="
                            + slices);
        }

        List<AmplitudeSlice> result = new ArrayList<>(slices);

        if (slices == 1) {
            // Serial case - single slice covering all amplitudes
            result.add(new AmplitudeSlice(0, nAmps));
            return result;
        }

        int baseChunkSize = nAmps / slices;
        int remainder = nAmps % slices;

        int currentStart = 0;
        for (int i = 0; i < slices; i++) {
            int chunkSize = baseChunkSize + (i < remainder ? 1 : 0);

            // Align chunk size to SIMD boundary if possible (except for last slice)
            if (i < slices - 1 && chunkSize % SIMD_ALIGNMENT != 0) {
                int aligned = ((chunkSize + SIMD_ALIGNMENT - 1) / SIMD_ALIGNMENT) * SIMD_ALIGNMENT;
                // Only align if it doesn't exceed remaining amplitudes
                if (currentStart + aligned < nAmps) {
                    chunkSize = aligned;
                }
            }

            int currentEnd = Math.min(currentStart + chunkSize, nAmps);
            result.add(new AmplitudeSlice(currentStart, currentEnd));
            currentStart = currentEnd;
        }

        // Adjust the last slice to cover any remaining amplitudes
        if (currentStart < nAmps) {
            AmplitudeSlice lastSlice = result.get(result.size() - 1);
            result.set(result.size() - 1, new AmplitudeSlice(lastSlice.start(), nAmps));
        }

        return result;
    }

    /**
     * Executes a kernel operation across amplitude slices in parallel.
     *
     * <p>This method coordinates the parallel execution of gate kernels using
     * StructuredTaskScope.ShutdownOnFailure. Each slice is processed by a separate thread, with
     * proper error handling and cancellation semantics.
     *
     * <p><strong>ERROR HANDLING:</strong> If any slice fails, all other slices are cancelled and
     * the exception is propagated to the caller.
     *
     * @param sv the state vector to operate on (shared across all slices)
     * @param nQubits the number of qubits in the circuit
     * @param sliceTask a consumer that processes each AmplitudeSlice
     * @throws RuntimeException if any slice processing fails
     * @throws InterruptedException if the thread is interrupted during execution
     */
    public static void forEachSlice(StateVector sv, int nQubits, Consumer<AmplitudeSlice> sliceTask)
            throws InterruptedException {

        int nAmps = sv.logicalSize();
        int numSlices = decideSlices(nAmps, nQubits);
        List<AmplitudeSlice> slices = slice(nAmps, numSlices);

        if (numSlices == 1) {
            // Serial execution - no need for StructuredTaskScope overhead
            sliceTask.accept(slices.get(0));
            return;
        }

        // Parallel execution using StructuredTaskScope
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // Fork tasks for each slice
            for (AmplitudeSlice slice : slices) {
                scope.fork(
                        () -> {
                            sliceTask.accept(slice);
                            return null; // StructuredTaskScope requires Callable<T>
                        });
            }

            // Wait for all tasks to complete
            scope.join();

            // Propagate any failures
            scope.throwIfFailed();
        } catch (java.util.concurrent.ExecutionException e) {
            // Wrap execution exceptions in RuntimeException for easier handling
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException re) {
                throw re;
            } else if (cause instanceof Error err) {
                throw err;
            } else {
                throw new RuntimeException("Parallel execution failed", cause);
            }
        }
    }

    /**
     * Convenience method for debugging slice distribution.
     *
     * @param nAmps total number of amplitudes
     * @param nQubits number of qubits
     * @return a string describing the slice distribution
     */
    public static String describeSlicing(int nAmps, int nQubits) {
        int numSlices = decideSlices(nAmps, nQubits);
        List<AmplitudeSlice> slices = slice(nAmps, numSlices);

        StringBuilder sb = new StringBuilder();
        sb.append(
                String.format(
                        "Slicing %d amplitudes (%d qubits) into %d slices:\n",
                        nAmps, nQubits, numSlices));

        for (int i = 0; i < slices.size(); i++) {
            AmplitudeSlice slice = slices.get(i);
            sb.append(String.format("  Slice %d: %s\n", i, slice));
        }

        return sb.toString();
    }
}
