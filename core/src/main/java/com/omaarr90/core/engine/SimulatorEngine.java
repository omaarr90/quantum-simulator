package com.omaarr90.core.engine;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.core.engine.result.SimulationResult;

/**
 * Service Provider Interface for quantum simulation engines.
 * 
 * <p>This interface defines the contract for quantum circuit simulation backends.
 * Implementations can provide different simulation strategies such as state-vector,
 * stabilizer, tensor-network, or other quantum simulation approaches.
 * 
 * <p>Implementations should be:
 * <ul>
 *   <li>stateless & thread-safe <strong>or</strong> clearly document thread-safety</li>
 *   <li>deterministic for a fixed PRNG seed (if noise is involved)</li>
 *   <li>able to throw {@link UnsupportedOperationException} for unsupported gates</li>
 * </ul>
 * 
 * <p>Engines are discovered at runtime via Java's {@link java.util.ServiceLoader}.
 * Each implementation must be registered in {@code META-INF/services/com.omaarr90.qsim.engine.SimulatorEngine}.
 * 
 * @see SimulationResult
 * @see java.util.ServiceLoader
 */
public interface SimulatorEngine {
    
    /**
     * Execute the given quantum circuit and return a backend-specific {@code SimulationResult}.
     * 
     * <p>The implementation should process all gate operations in the circuit and
     * perform measurements as specified in the circuit's measurement map.
     * 
     * @param circuit the quantum circuit to simulate
     * @return the simulation result containing measurement outcomes and backend-specific data
     * @throws UnsupportedOperationException if the circuit contains unsupported gates
     * @throws IllegalArgumentException if the circuit is invalid for this engine
     * @throws NullPointerException if circuit is null
     */
    SimulationResult run(Circuit circuit);
    
    /**
     * Returns a unique snake-case identifier for this simulation engine.
     * 
     * <p>Examples: "statevector", "stabilizer", "tensor_network"
     * 
     * <p>This identifier is used by {@link SimulatorEngineRegistry}
     * to look up specific engine implementations.
     * 
     * @return unique identifier for this engine, must not be null or empty
     */
    String id();
}