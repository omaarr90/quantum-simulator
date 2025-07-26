package com.omaarr90.core.engine.result;

import java.util.Map;

/**
 * Sealed interface representing the result of a quantum circuit simulation.
 * 
 * <p>This interface provides a common contract for all simulation results while
 * allowing different engine implementations to extend with backend-specific details.
 * The sealed nature ensures type safety and enables pattern matching.
 * 
 * <p>All implementations must provide measurement counts and shot information,
 * which are the fundamental outputs of any quantum simulation.
 * 
 * @see StateVectorResult
 * @see StabilizerResult
 * @see TensorNetworkResult
 */
public sealed interface SimulationResult
        permits StateVectorResult, StabilizerResult, TensorNetworkResult {

    /**
     * Returns a map of measurement outcomes to their occurrence counts.
     * 
     * <p>The keys are bit-strings representing measurement outcomes (e.g., "00", "01", "10", "11"
     * for a 2-qubit measurement), and the values are the number of times each outcome was observed.
     * 
     * <p>For deterministic simulations (single shot), each outcome will have a count of 1.
     * For probabilistic simulations with multiple shots, the counts represent the frequency
     * of each outcome across all shots.
     * 
     * @return immutable map of bit-string outcomes to their counts, never null
     */
    Map<String, Long> counts();

    /**
     * Returns the total number of measurement shots performed.
     * 
     * <p>For single-shot simulations, this returns 1. For multi-shot simulations,
     * this returns the total number of circuit executions performed.
     * 
     * <p>The sum of all values in {@link #counts()} should equal this value.
     * 
     * @return total number of shots, always positive
     */
    default int totalShots() {
        return 1;
    }
}