package com.omaarr90.core.circuit;

import com.omaarr90.core.gate.GateType;

/**
 * Immutable representation of a quantum gate application. This sealed interface represents
 * operations that can be applied in a quantum circuit.
 */
public sealed interface GateOp permits GateOp.Gate, GateOp.Barrier {

    /**
     * Represents a quantum gate application with specific qubits and optional parameters. Gate
     * matrix is looked up via GateType when needed (lazy).
     *
     * @param type the type of quantum gate
     * @param qubits the qubits this gate operates on
     * @param params optional parameters for parameterized gates (e.g., rotation angles)
     */
    record Gate(GateType type, int[] qubits, double... params) implements GateOp {
        public Gate {
            // Defensive copy of qubits array to ensure immutability
            qubits = qubits.clone();
            // Defensive copy of params array to ensure immutability
            params = params.clone();
        }

        /** Convenience constructor for single-qubit gates without parameters. */
        public Gate(GateType type, int qubit) {
            this(type, new int[] {qubit});
        }

        /** Convenience constructor for single-qubit parameterized gates. */
        public Gate(GateType type, int qubit, double param) {
            this(type, new int[] {qubit}, param);
        }

        /** Convenience constructor for two-qubit gates. */
        public Gate(GateType type, int qubit1, int qubit2) {
            this(type, new int[] {qubit1, qubit2});
        }
    }

    /**
     * Represents a barrier operation for visual/logical separation in the circuit. Barriers do not
     * affect the quantum computation but provide structure.
     *
     * @param qubits the qubits this barrier applies to
     */
    record Barrier(int[] qubits) implements GateOp {
        public Barrier {
            // Defensive copy of qubits array to ensure immutability
            qubits = qubits.clone();
        }

        /** Convenience constructor for full barrier (all qubits). */
        public Barrier() {
            this(new int[0]); // Empty array indicates full barrier
        }
    }
}
