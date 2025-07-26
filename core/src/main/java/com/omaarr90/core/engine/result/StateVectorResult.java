package com.omaarr90.core.engine.result;

import java.util.Map;

/**
 * Simulation result from a state-vector quantum simulation engine.
 * 
 * <p>This record contains the complete quantum state information including
 * complex amplitudes for all basis states, along with measurement outcomes.
 * 
 * <p>The amplitudes array represents the quantum state vector where
 * {@code amplitudes[i]} corresponds to the amplitude of the i-th basis state
 * in computational basis ordering (e.g., |00⟩, |01⟩, |10⟩, |11⟩ for 2 qubits).
 * 
 * @param amplitudes complex amplitudes of the quantum state vector, must not be null
 * @param counts map of measurement outcomes to their occurrence counts, must not be null
 * @param totalShots total number of measurement shots performed, must be positive
 */
public record StateVectorResult(
        double[] amplitudes,
        Map<String, Long> counts,
        int totalShots
) implements SimulationResult {
    
    /**
     * Compact constructor with validation.
     */
    public StateVectorResult {
        if (amplitudes == null) {
            throw new IllegalArgumentException("Amplitudes array cannot be null");
        }
        if (counts == null) {
            throw new IllegalArgumentException("Counts map cannot be null");
        }
        if (totalShots <= 0) {
            throw new IllegalArgumentException("Total shots must be positive: " + totalShots);
        }
        
        // Defensive copy to ensure immutability
        amplitudes = amplitudes.clone();
        counts = Map.copyOf(counts);
        
        // Validate that amplitudes array length corresponds to 2^n complex amplitudes
        // For complex numbers stored as [real0, imag0, real1, imag1, ...], length should be 2 * 2^n
        int length = amplitudes.length;
        if (length == 0 || length % 2 != 0 || ((length / 2) & ((length / 2) - 1)) != 0) {
            throw new IllegalArgumentException(
                "Amplitudes array length must be 2 * 2^n for n qubits, got: " + length);
        }
        
        // Validate that counts sum equals totalShots
        long countsSum = counts.values().stream().mapToLong(Long::longValue).sum();
        if (countsSum != totalShots) {
            throw new IllegalArgumentException(
                "Sum of counts (" + countsSum + ") must equal totalShots (" + totalShots + ")");
        }
    }
    
    /**
     * Returns the number of qubits represented by this state vector.
     * 
     * @return number of qubits
     */
    public int qubitCount() {
        return Integer.numberOfTrailingZeros(amplitudes.length / 2);
    }
    
    /**
     * Returns the probability of measuring the given basis state.
     * 
     * @param basisState the basis state index (0 to 2^n - 1)
     * @return probability of measuring this state
     * @throws IndexOutOfBoundsException if basisState is out of range
     */
    public double probability(int basisState) {
        int numStates = amplitudes.length / 2;
        if (basisState < 0 || basisState >= numStates) {
            throw new IndexOutOfBoundsException(
                "Basis state " + basisState + " out of range [0, " + (numStates - 1) + "]");
        }
        
        // For complex numbers stored as [real, imag, real, imag, ...], 
        // probability = |amplitude|^2 = real^2 + imag^2
        double real = amplitudes[2 * basisState];
        double imag = amplitudes[2 * basisState + 1];
        return real * real + imag * imag;
    }
}