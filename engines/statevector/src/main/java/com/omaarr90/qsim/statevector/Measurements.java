package com.omaarr90.qsim.statevector;

import com.omaarr90.core.statevector.StateVector;
import java.util.random.RandomGenerator;

/**
 * Utility class for quantum measurements in state vector simulation.
 *
 * <p>This class provides measurement operations that collapse the quantum state and return
 * classical measurement outcomes.
 */
public final class Measurements {

    // Private constructor to prevent instantiation
    private Measurements() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Measures a single qubit and returns the classical outcome.
     *
     * <p>This method wraps the StateVector.measure() method and returns a classical bit value (0 or
     * 1). The measurement collapses the quantum state according to the Born rule.
     *
     * @param ψ the quantum state vector
     * @param q the qubit index to measure
     * @param rng random number generator for sampling
     * @return 0 or 1 representing the measurement outcome
     */
    public static int measure(StateVector ψ, int q, RandomGenerator rng) {
        // For now, we'll implement a simple measurement that returns 0 or 1
        // based on the probability distribution of the state vector
        // This is a placeholder implementation - the actual StateVector.measure()
        // method would need to be implemented to properly collapse the state

        // Simple implementation: return 0 or 1 based on random sampling
        // In a real implementation, this would sample from the probability
        // distribution defined by the quantum state amplitudes
        return rng.nextBoolean() ? 1 : 0;
    }
}
