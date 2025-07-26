package com.omaarr90.qsim.noop;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.core.engine.SimulatorEngine;
import com.omaarr90.core.engine.result.SimulationResult;

/**
 * No-operation quantum simulation engine.
 *
 * <p>This engine performs no actual simulation and always returns an empty result with zero shots
 * and no measurement outcomes. It is useful for testing, benchmarking, or as a placeholder when no
 * simulation is needed.
 *
 * <p>This implementation is thread-safe and stateless. Multiple threads can safely call the {@link
 * #run(Circuit)} method concurrently.
 *
 * <p>All circuits are accepted but no actual computation is performed.
 */
public final class NoOpEngine implements SimulatorEngine {

    private static final String ENGINE_ID = "noop";

    @Override
    public SimulationResult run(Circuit circuit) {
        if (circuit == null) {
            throw new NullPointerException("Circuit cannot be null");
        }

        // No-op: return empty result without performing any simulation
        return SimulationResult.EMPTY;
    }

    @Override
    public String id() {
        return ENGINE_ID;
    }
}
