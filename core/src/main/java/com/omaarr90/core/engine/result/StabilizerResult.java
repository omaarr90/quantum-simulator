package com.omaarr90.core.engine.result;

import java.util.Map;

/**
 * Simulation result from a stabilizer quantum simulation engine.
 * 
 * <p>This record contains the stabilizer tableau representation and measurement outcomes.
 * Stabilizer simulations are efficient for circuits containing only Clifford gates
 * (Pauli gates, Hadamard, CNOT, and S gates).
 * 
 * <p>This is a placeholder implementation that will be extended when the stabilizer
 * engine is fully implemented.
 * 
 * @param stabilizers the stabilizer generators (placeholder for now)
 * @param counts map of measurement outcomes to their occurrence counts, must not be null
 * @param totalShots total number of measurement shots performed, must be positive
 */
public record StabilizerResult(
        String[] stabilizers, // Placeholder - will be replaced with proper stabilizer representation
        Map<String, Long> counts,
        int totalShots
) implements SimulationResult {
    
    /**
     * Compact constructor with validation.
     */
    public StabilizerResult {
        if (stabilizers == null) {
            throw new IllegalArgumentException("Stabilizers array cannot be null");
        }
        if (counts == null) {
            throw new IllegalArgumentException("Counts map cannot be null");
        }
        if (totalShots <= 0) {
            throw new IllegalArgumentException("Total shots must be positive: " + totalShots);
        }
        
        // Defensive copy to ensure immutability
        stabilizers = stabilizers.clone();
        counts = Map.copyOf(counts);
        
        // Validate that counts sum equals totalShots
        long countsSum = counts.values().stream().mapToLong(Long::longValue).sum();
        if (countsSum != totalShots) {
            throw new IllegalArgumentException(
                "Sum of counts (" + countsSum + ") must equal totalShots (" + totalShots + ")");
        }
    }
}