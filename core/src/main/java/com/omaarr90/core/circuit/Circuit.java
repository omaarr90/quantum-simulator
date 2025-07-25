package com.omaarr90.core.circuit;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Immutable representation of a quantum circuit.
 * Contains all the information needed to describe a complete quantum circuit
 * including gates, measurements, and metadata.
 * 
 * This record is thread-safe and can be safely shared between threads.
 * All collections are immutable to guarantee thread safety.
 * 
 * @param qubitCount the number of qubits in this circuit
 * @param ops the ordered list of gate operations in this circuit
 * @param classicalBits the number of classical bits allocated for measurements
 * @param measureMap mapping from qubit index to classical bit index for measurements
 * @param createdAt timestamp when this circuit was created
 */
public record Circuit(
        int qubitCount,
        List<GateOp> ops,
        int classicalBits,
        Map<Integer, Integer> measureMap,
        Instant createdAt
) {
    
    /**
     * Compact constructor with validation.
     */
    public Circuit {
        if (qubitCount < 0) {
            throw new IllegalArgumentException("Qubit count cannot be negative: " + qubitCount);
        }
        if (classicalBits < 0) {
            throw new IllegalArgumentException("Classical bits count cannot be negative: " + classicalBits);
        }
        if (ops == null) {
            throw new IllegalArgumentException("Operations list cannot be null");
        }
        if (measureMap == null) {
            throw new IllegalArgumentException("Measurement map cannot be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Creation timestamp cannot be null");
        }
        
        // Ensure immutability by creating defensive copies if needed
        ops = List.copyOf(ops);
        measureMap = Map.copyOf(measureMap);
        
        // Validate measurement map entries
        for (Map.Entry<Integer, Integer> entry : measureMap.entrySet()) {
            int qubit = entry.getKey();
            int cbit = entry.getValue();
            
            if (qubit < 0 || qubit >= qubitCount) {
                throw new IllegalArgumentException(
                    "Invalid qubit index in measurement map: " + qubit + 
                    " (circuit has " + qubitCount + " qubits)");
            }
            if (cbit < 0 || cbit >= classicalBits) {
                throw new IllegalArgumentException(
                    "Invalid classical bit index in measurement map: " + cbit + 
                    " (circuit has " + classicalBits + " classical bits)");
            }
        }
        
        // Validate gate operations
        for (GateOp op : ops) {
            if (op instanceof GateOp.Gate gate) {
                for (int qubit : gate.qubits()) {
                    if (qubit < 0 || qubit >= qubitCount) {
                        throw new IllegalArgumentException(
                            "Invalid qubit index in gate operation: " + qubit + 
                            " (circuit has " + qubitCount + " qubits)");
                    }
                }
            } else if (op instanceof GateOp.Barrier barrier) {
                // Empty qubits array means full barrier, which is always valid
                if (barrier.qubits().length > 0) {
                    for (int qubit : barrier.qubits()) {
                        if (qubit < 0 || qubit >= qubitCount) {
                            throw new IllegalArgumentException(
                                "Invalid qubit index in barrier operation: " + qubit + 
                                " (circuit has " + qubitCount + " qubits)");
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Returns the number of gate operations in this circuit.
     * 
     * @return the number of operations
     */
    public int operationCount() {
        return ops.size();
    }
    
    /**
     * Returns the number of measurements in this circuit.
     * 
     * @return the number of measurements
     */
    public int measurementCount() {
        return measureMap.size();
    }
    
    /**
     * Checks if this circuit has any measurements.
     * 
     * @return true if there are measurements, false otherwise
     */
    public boolean hasMeasurements() {
        return !measureMap.isEmpty();
    }
}