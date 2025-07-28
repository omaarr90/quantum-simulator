package com.omaarr90.core.engine.result;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simulation result from a state-vector quantum simulation engine.
 *
 * <p>This record contains the complete quantum state information including complex amplitudes for
 * all basis states, along with measurement outcomes.
 *
 * <p>The amplitudes array represents the quantum state vector where {@code amplitudes[i]}
 * corresponds to the amplitude of the i-th basis state in computational basis ordering (e.g., |00⟩,
 * |01⟩, |10⟩, |11⟩ for 2 qubits).
 *
 * @param amplitudes complex amplitudes of the quantum state vector, must not be null
 * @param counts map of measurement outcomes to their occurrence counts, must not be null
 * @param totalShots total number of measurement shots performed, must be positive
 * @param gateCount total number of gates applied during simulation, must be non-negative
 * @param elapsed time taken for the simulation, must not be null
 */
public record StateVectorResult(
        double[] amplitudes,
        Map<String, Long> counts,
        int totalShots,
        long gateCount,
        Duration elapsed)
        implements SimulationResult {

    /** Compact constructor with validation. */
    public StateVectorResult {
        if (amplitudes == null) {
            throw new IllegalArgumentException("Amplitudes array cannot be null");
        }
        if (counts == null) {
            throw new IllegalArgumentException("Counts map cannot be null");
        }
        if (totalShots < 0 || (totalShots == 0 && !counts.isEmpty())) {
            throw new IllegalArgumentException(
                    "Total shots must be positive when measurements are present: " + totalShots);
        }
        if (gateCount < 0) {
            throw new IllegalArgumentException("Gate count must be non-negative: " + gateCount);
        }
        if (elapsed == null) {
            throw new IllegalArgumentException("Elapsed time cannot be null");
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

    /**
     * Creates a new builder for StateVectorResult.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for StateVectorResult with fluent API. */
    public static final class Builder {
        private double[] amplitudes;
        private Map<String, Integer> counts = new HashMap<>();
        private int shots = 1;
        private long gateCount = 0;
        private Duration elapsed = Duration.ZERO;
        private boolean stateVectorIncluded = false;

        private Builder() {}

        /**
         * Sets the quantum state amplitudes.
         *
         * @param amplitudes the complex amplitudes array
         * @return this builder
         */
        public Builder amplitudes(double[] amplitudes) {
            this.amplitudes = amplitudes;
            return this;
        }

        /**
         * Sets the measurement counts.
         *
         * @param counts map of measurement outcomes to counts
         * @return this builder
         */
        public Builder counts(Map<String, Integer> counts) {
            this.counts = new HashMap<>(counts);
            return this;
        }

        /**
         * Sets the number of shots.
         *
         * @param shots number of measurement shots
         * @return this builder
         */
        public Builder shots(int shots) {
            this.shots = shots;
            return this;
        }

        /**
         * Sets the gate count.
         *
         * @param gateCount number of gates applied
         * @return this builder
         */
        public Builder gateCount(long gateCount) {
            this.gateCount = gateCount;
            return this;
        }

        /**
         * Sets the elapsed time.
         *
         * @param elapsed simulation duration
         * @return this builder
         */
        public Builder elapsed(Duration elapsed) {
            this.elapsed = elapsed;
            return this;
        }

        /**
         * Sets whether the state vector should be included in the result.
         *
         * @param included whether to include state vector
         * @return this builder
         */
        public Builder stateVectorIncluded(boolean included) {
            this.stateVectorIncluded = included;
            return this;
        }

        /**
         * Builds the StateVectorResult.
         *
         * @return the constructed StateVectorResult
         */
        public StateVectorResult build() {
            // Convert counts to Long values for the record constructor
            Map<String, Long> longCounts =
                    counts.entrySet().stream()
                            .collect(
                                    Collectors.toMap(
                                            Map.Entry::getKey,
                                            entry -> Long.valueOf(entry.getValue())));

            // If state vector not included, create minimal valid array
            double[] resultAmplitudes;
            if (stateVectorIncluded && amplitudes != null) {
                resultAmplitudes = amplitudes;
            } else {
                // Create minimal valid array for 1 qubit (2 complex amplitudes = 4 doubles)
                resultAmplitudes = new double[4];
                resultAmplitudes[0] = 1.0; // |0⟩ state has amplitude 1
            }

            return new StateVectorResult(resultAmplitudes, longCounts, shots, gateCount, elapsed);
        }
    }
}
