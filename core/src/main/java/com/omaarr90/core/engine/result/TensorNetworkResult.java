package com.omaarr90.core.engine.result;

import java.util.Map;

/**
 * Simulation result from a tensor-network quantum simulation engine.
 *
 * <p>This record contains tensor network representation and measurement outcomes. Tensor network
 * simulations can efficiently handle certain classes of quantum circuits by exploiting the
 * entanglement structure and using techniques like Matrix Product States (MPS).
 *
 * <p>This is a placeholder implementation that will be extended when the tensor network engine is
 * fully implemented.
 *
 * @param bondDimensions the bond dimensions of the tensor network (placeholder for now)
 * @param counts map of measurement outcomes to their occurrence counts, must not be null
 * @param totalShots total number of measurement shots performed, must be positive
 */
public record TensorNetworkResult(
        int[] bondDimensions, // Placeholder - will be replaced with proper tensor network
        // representation
        Map<String, Long> counts,
        int totalShots)
        implements SimulationResult {

    /** Compact constructor with validation. */
    public TensorNetworkResult {
        if (bondDimensions == null) {
            throw new IllegalArgumentException("Bond dimensions array cannot be null");
        }
        if (counts == null) {
            throw new IllegalArgumentException("Counts map cannot be null");
        }
        if (totalShots <= 0) {
            throw new IllegalArgumentException("Total shots must be positive: " + totalShots);
        }

        // Defensive copy to ensure immutability
        bondDimensions = bondDimensions.clone();
        counts = Map.copyOf(counts);

        // Validate that counts sum equals totalShots
        long countsSum = counts.values().stream().mapToLong(Long::longValue).sum();
        if (countsSum != totalShots) {
            throw new IllegalArgumentException(
                    "Sum of counts (" + countsSum + ") must equal totalShots (" + totalShots + ")");
        }
    }
}
