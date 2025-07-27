package com.omaarr90.qsim.statevector.parallel;

/**
 * Represents a contiguous slice of amplitude indices for parallel processing.
 *
 * <p>This record defines a half-open interval [start, end) that represents a chunk of quantum state
 * amplitudes to be processed by a single thread. The slice boundaries are designed to be
 * cache-aligned and avoid false sharing between parallel workers.
 *
 * <p><strong>IMMUTABILITY GUARANTEE:</strong> This record is completely immutable and thread-safe.
 *
 * <p><strong>PERFORMANCE NOTE:</strong> Slice boundaries should be aligned to SIMD vector
 * boundaries (typically multiples of 8 or 16 doubles) to maintain vectorization efficiency within
 * each slice.
 *
 * @param start the inclusive start index of the amplitude slice
 * @param end the exclusive end index of the amplitude slice
 */
public record AmplitudeSlice(int start, int end) {

    /**
     * Creates a new AmplitudeSlice with validation.
     *
     * @param start the inclusive start index
     * @param end the exclusive end index
     * @throws IllegalArgumentException if start >= end or if indices are negative
     */
    public AmplitudeSlice {
        if (start < 0) {
            throw new IllegalArgumentException("Start index cannot be negative: " + start);
        }
        if (end < 0) {
            throw new IllegalArgumentException("End index cannot be negative: " + end);
        }
        if (start >= end) {
            throw new IllegalArgumentException(
                    "Start index must be less than end index: start=" + start + ", end=" + end);
        }
    }

    /**
     * Returns the number of amplitudes in this slice.
     *
     * @return the length of the slice (end - start)
     */
    public int length() {
        return end - start;
    }

    /**
     * Checks if this slice contains the given index.
     *
     * @param index the index to check
     * @return true if start <= index < end
     */
    public boolean contains(int index) {
        return index >= start && index < end;
    }

    /**
     * Returns a string representation suitable for debugging.
     *
     * @return a string in the format "[start, end) length=N"
     */
    @Override
    public String toString() {
        return String.format("[%d, %d) length=%d", start, end, length());
    }
}
