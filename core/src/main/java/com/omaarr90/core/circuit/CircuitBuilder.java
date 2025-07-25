package com.omaarr90.core.circuit;

import com.omaarr90.core.gate.GateType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Fluent builder for constructing quantum circuits.
 * 
 * This builder provides a type-safe, chainable API for assembling quantum circuits
 * programmatically. It mirrors common QASM syntax while remaining intrinsically
 * friendly and GraalVM native-image compatible.
 * 
 * The builder is thread-confined (not thread-safe), but the resulting Circuit
 * is immutable and thread-safe.
 * 
 * Example usage:
 * <pre>{@code
 * var circuit = CircuitBuilder.of(3)
 *         .h(0)
 *         .cx(0, 1)
 *         .rz(1, Math.PI / 2)
 *         .barrier()
 *         .measureAll()
 *         .build();
 * }</pre>
 */
public final class CircuitBuilder {
    
    private final int qubitCount;
    private final List<GateOp> operations;
    private final Map<Integer, Integer> measurementMap;
    private final Instant createdAt;
    private int classicalBits;
    
    /**
     * Private constructor. Use {@link #of(int)} to create instances.
     */
    private CircuitBuilder(int numQubits) {
        if (numQubits < 0) {
            throw new IllegalArgumentException("Number of qubits cannot be negative: " + numQubits);
        }
        this.qubitCount = numQubits;
        this.operations = new ArrayList<>();
        this.measurementMap = new HashMap<>();
        this.classicalBits = 0;
        this.createdAt = Instant.now();
    }
    
    /**
     * Creates a new circuit builder for the specified number of qubits.
     * 
     * @param numQubits the number of qubits in the circuit
     * @return a new CircuitBuilder instance
     * @throws IllegalArgumentException if numQubits is negative
     */
    public static CircuitBuilder of(int numQubits) {
        return new CircuitBuilder(numQubits);
    }
    
    // ========== Validation Helpers ==========
    
    /**
     * Validates that a qubit index is within bounds.
     */
    private void checkQubit(int qubit) {
        if (qubit < 0 || qubit >= qubitCount) {
            throw new IllegalArgumentException(
                "Qubit index " + qubit + " is out of range [0, " + (qubitCount - 1) + "]");
        }
    }
    
    /**
     * Validates multiple qubit indices.
     */
    private void checkQubits(int... qubits) {
        for (int qubit : qubits) {
            checkQubit(qubit);
        }
    }
    
    // ========== Single-Qubit Gates ==========
    
    /**
     * Applies a Hadamard gate to the specified qubit.
     * 
     * @param qubit the target qubit
     * @return this builder for chaining
     */
    public CircuitBuilder h(int qubit) {
        checkQubit(qubit);
        operations.add(new GateOp.Gate(GateType.H, qubit));
        return this;
    }
    
    /**
     * Applies a Pauli-X gate to the specified qubit.
     * 
     * @param qubit the target qubit
     * @return this builder for chaining
     */
    public CircuitBuilder x(int qubit) {
        checkQubit(qubit);
        operations.add(new GateOp.Gate(GateType.X, qubit));
        return this;
    }
    
    /**
     * Applies a Pauli-Y gate to the specified qubit.
     * 
     * @param qubit the target qubit
     * @return this builder for chaining
     */
    public CircuitBuilder y(int qubit) {
        checkQubit(qubit);
        operations.add(new GateOp.Gate(GateType.Y, qubit));
        return this;
    }
    
    /**
     * Applies a Pauli-Z gate to the specified qubit.
     * 
     * @param qubit the target qubit
     * @return this builder for chaining
     */
    public CircuitBuilder z(int qubit) {
        checkQubit(qubit);
        operations.add(new GateOp.Gate(GateType.Z, qubit));
        return this;
    }
    
    /**
     * Applies an RX rotation gate to the specified qubit.
     * 
     * @param qubit the target qubit
     * @param theta the rotation angle in radians
     * @return this builder for chaining
     */
    public CircuitBuilder rx(int qubit, double theta) {
        checkQubit(qubit);
        operations.add(new GateOp.Gate(GateType.RX, qubit, theta));
        return this;
    }
    
    /**
     * Applies an RY rotation gate to the specified qubit.
     * 
     * @param qubit the target qubit
     * @param theta the rotation angle in radians
     * @return this builder for chaining
     */
    public CircuitBuilder ry(int qubit, double theta) {
        checkQubit(qubit);
        operations.add(new GateOp.Gate(GateType.RY, qubit, theta));
        return this;
    }
    
    /**
     * Applies an RZ rotation gate to the specified qubit.
     * 
     * @param qubit the target qubit
     * @param theta the rotation angle in radians
     * @return this builder for chaining
     */
    public CircuitBuilder rz(int qubit, double theta) {
        checkQubit(qubit);
        operations.add(new GateOp.Gate(GateType.RZ, qubit, theta));
        return this;
    }
    
    // ========== Multi-Qubit Gates ==========
    
    /**
     * Applies a CNOT (controlled-X) gate.
     * 
     * @param control the control qubit
     * @param target the target qubit
     * @return this builder for chaining
     */
    public CircuitBuilder cx(int control, int target) {
        checkQubits(control, target);
        if (control == target) {
            throw new IllegalArgumentException("Control and target qubits must be different");
        }
        operations.add(new GateOp.Gate(GateType.CX, control, target));
        return this;
    }
    
    /**
     * Applies a controlled-Z gate.
     * 
     * @param control the control qubit
     * @param target the target qubit
     * @return this builder for chaining
     */
    public CircuitBuilder cz(int control, int target) {
        checkQubits(control, target);
        if (control == target) {
            throw new IllegalArgumentException("Control and target qubits must be different");
        }
        operations.add(new GateOp.Gate(GateType.CZ, control, target));
        return this;
    }
    
    /**
     * Applies a SWAP gate.
     * 
     * @param qubit1 the first qubit
     * @param qubit2 the second qubit
     * @return this builder for chaining
     */
    public CircuitBuilder swap(int qubit1, int qubit2) {
        checkQubits(qubit1, qubit2);
        if (qubit1 == qubit2) {
            throw new IllegalArgumentException("SWAP qubits must be different");
        }
        operations.add(new GateOp.Gate(GateType.SWAP, qubit1, qubit2));
        return this;
    }
    
    // ========== Generic Gate Methods ==========
    
    /**
     * Applies a gate of the specified type to the given qubits.
     * 
     * @param type the gate type
     * @param qubits the target qubits
     * @return this builder for chaining
     */
    public CircuitBuilder gate(GateType type, int... qubits) {
        checkQubits(qubits);
        operations.add(new GateOp.Gate(type, qubits.clone()));
        return this;
    }
    
    /**
     * Applies a parameterized gate of the specified type to the given qubits.
     * 
     * @param type the gate type
     * @param param the parameter (e.g., rotation angle)
     * @param qubits the target qubits
     * @return this builder for chaining
     */
    public CircuitBuilder gate(GateType type, double param, int... qubits) {
        checkQubits(qubits);
        operations.add(new GateOp.Gate(type, qubits.clone(), param));
        return this;
    }
    
    // ========== Barriers ==========
    
    /**
     * Adds a barrier across all qubits for visual/logical separation.
     * 
     * @return this builder for chaining
     */
    public CircuitBuilder barrier() {
        operations.add(new GateOp.Barrier());
        return this;
    }
    
    /**
     * Adds a barrier across the specified qubits.
     * 
     * @param qubits the qubits to include in the barrier
     * @return this builder for chaining
     */
    public CircuitBuilder barrier(int... qubits) {
        checkQubits(qubits);
        operations.add(new GateOp.Barrier(qubits.clone()));
        return this;
    }
    
    // ========== Measurements ==========
    
    /**
     * Adds a measurement of the specified qubit to the specified classical bit.
     * Auto-expands the classical register if needed.
     * 
     * @param qubit the qubit to measure
     * @param cbit the classical bit to store the result
     * @return this builder for chaining
     * @throws IllegalArgumentException if the qubit is already measured
     */
    public CircuitBuilder measure(int qubit, int cbit) {
        checkQubit(qubit);
        if (cbit < 0) {
            throw new IllegalArgumentException("Classical bit index cannot be negative: " + cbit);
        }
        if (measurementMap.containsKey(qubit)) {
            throw new IllegalArgumentException("Qubit " + qubit + " is already measured");
        }
        
        measurementMap.put(qubit, cbit);
        // Auto-expand classical register if needed
        classicalBits = Math.max(classicalBits, cbit + 1);
        return this;
    }
    
    /**
     * Adds measurements for all qubits, mapping qubit i to classical bit i.
     * 
     * @return this builder for chaining
     */
    public CircuitBuilder measureAll() {
        for (int i = 0; i < qubitCount; i++) {
            if (!measurementMap.containsKey(i)) {
                measurementMap.put(i, i);
            }
        }
        classicalBits = Math.max(classicalBits, qubitCount);
        return this;
    }
    
    // ========== Classical Controls (Optional) ==========
    
    /**
     * Conditionally applies operations based on a classical bit value.
     * The nested builder shares the parent's metadata and state.
     * 
     * @param cbit the classical bit to check
     * @param value the expected value (0 or 1)
     * @param gatedOps consumer that adds operations to apply if condition is met
     * @return this builder for chaining
     */
    public CircuitBuilder cIf(int cbit, int value, Consumer<CircuitBuilder> gatedOps) {
        if (cbit < 0) {
            throw new IllegalArgumentException("Classical bit index cannot be negative: " + cbit);
        }
        if (value != 0 && value != 1) {
            throw new IllegalArgumentException("Classical bit value must be 0 or 1: " + value);
        }
        
        // For now, we'll add the gated operations unconditionally
        // In a real implementation, this would be handled by the simulator
        // based on the classical bit state during execution
        gatedOps.accept(this);
        return this;
    }
    
    // ========== Build Method ==========
    
    /**
     * Builds and returns an immutable Circuit from the current builder state.
     * 
     * @return an immutable Circuit instance
     */
    public Circuit build() {
        return new Circuit(
            qubitCount,
            List.copyOf(operations),
            classicalBits,
            Map.copyOf(measurementMap),
            createdAt
        );
    }
    
    // ========== Debug Helpers ==========
    
    /**
     * Returns the current number of operations in the builder.
     * 
     * @return the operation count
     */
    public int operationCount() {
        return operations.size();
    }
    
    /**
     * Returns the current number of measurements in the builder.
     * 
     * @return the measurement count
     */
    public int measurementCount() {
        return measurementMap.size();
    }
}