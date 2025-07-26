package com.omaarr90.core.engine.result;

import java.util.Map;

/**
 * Empty simulation result with zero shots and no measurement outcomes.
 * 
 * <p>This result is used by no-op engines or when no simulation is performed.
 * It represents a valid but empty result state.
 */
public record EmptyResult() implements SimulationResult {
    
    @Override
    public Map<String, Long> counts() {
        return Map.of();
    }
    
    @Override
    public int totalShots() {
        return 0;
    }
}