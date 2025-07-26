package com.omaarr90.core.gate;

import java.util.Set;

/**
 * Enumeration of quantum gate types representing the universal gate set.
 *
 * <p>This enum provides quantum gate types with their matrix representations and utility methods.
 * Gates are categorized as either fixed (non-parameterized) or parameterized (rotation gates).
 *
 * <h3>Fixed Gates</h3>
 *
 * <ul>
 *   <li><b>H (Hadamard)</b>: Creates superposition
 *       <pre>H = (1/√2) * [[1,  1],
 *                  [1, -1]]</pre>
 *   <li><b>X (Pauli-X)</b>: Bit flip gate
 *       <pre>X = [[0, 1],
 *          [1, 0]]</pre>
 *   <li><b>Y (Pauli-Y)</b>: Bit and phase flip gate
 *       <pre>Y = [[0, -i],
 *          [i,  0]]</pre>
 *   <li><b>Z (Pauli-Z)</b>: Phase flip gate
 *       <pre>Z = [[1,  0],
 *          [0, -1]]</pre>
 *   <li><b>S (Phase)</b>: Quarter phase gate
 *       <pre>S = [[1, 0],
 *          [0, i]]</pre>
 *   <li><b>SDG (S-dagger)</b>: Conjugate transpose of S gate
 *       <pre>SDG = [[1,  0],
 *            [0, -i]]</pre>
 *   <li><b>T (π/8)</b>: Eighth phase gate
 *       <pre>T = [[1,           0],
 *          [0, e^(iπ/4)]]</pre>
 *   <li><b>TDG (T-dagger)</b>: Conjugate transpose of T gate
 *       <pre>TDG = [[1,            0],
 *            [0, e^(-iπ/4)]]</pre>
 *   <li><b>CX (CNOT)</b>: Controlled-X gate
 *       <pre>CX = [[1, 0, 0, 0],
 *           [0, 1, 0, 0],
 *           [0, 0, 0, 1],
 *           [0, 0, 1, 0]]</pre>
 *   <li><b>CZ (Controlled-Z)</b>: Controlled-Z gate
 *       <pre>CZ = [[1, 0, 0,  0],
 *           [0, 1, 0,  0],
 *           [0, 0, 1,  0],
 *           [0, 0, 0, -1]]</pre>
 *   <li><b>SWAP</b>: Qubit swap gate
 *       <pre>SWAP = [[1, 0, 0, 0],
 *             [0, 0, 1, 0],
 *             [0, 1, 0, 0],
 *             [0, 0, 0, 1]]</pre>
 * </ul>
 *
 * <h3>Parameterized Gates</h3>
 *
 * <ul>
 *   <li><b>RX(θ)</b>: Rotation around X-axis
 *       <pre>RX(θ) = [[cos(θ/2), -i*sin(θ/2)],
 *              [-i*sin(θ/2), cos(θ/2)]]</pre>
 *   <li><b>RY(θ)</b>: Rotation around Y-axis
 *       <pre>RY(θ) = [[cos(θ/2), -sin(θ/2)],
 *              [sin(θ/2),  cos(θ/2)]]</pre>
 *   <li><b>RZ(θ)</b>: Rotation around Z-axis
 *       <pre>RZ(θ) = [[e^(-iθ/2),        0],
 *              [0,        e^(iθ/2)]]</pre>
 * </ul>
 */
public enum GateType {
    /** Hadamard gate - creates superposition */
    H,
    /** Pauli-X gate - bit flip */
    X,
    /** Pauli-Y gate - bit and phase flip */
    Y,
    /** Pauli-Z gate - phase flip */
    Z,
    /** S gate - quarter phase gate (√Z) */
    S,
    /** SDG gate - conjugate transpose of S gate */
    SDG,
    /** T gate - eighth phase gate (√S) */
    T,
    /** TDG gate - conjugate transpose of T gate */
    TDG,
    /** RX gate - parameterized rotation around X-axis */
    RX,
    /** RY gate - parameterized rotation around Y-axis */
    RY,
    /** RZ gate - parameterized rotation around Z-axis */
    RZ,
    /** CNOT gate - controlled-X */
    CX,
    /** Controlled-Z gate */
    CZ,
    /** SWAP gate - exchanges two qubits */
    SWAP;

    /** Set of parameterized (rotation) gate types */
    private static final Set<GateType> PARAMETERIZED_GATES = Set.of(RX, RY, RZ);

    /**
     * Determines if this gate type is parameterized (requires a theta parameter).
     *
     * @return true if this gate is parameterized (RX, RY, RZ), false otherwise
     */
    public boolean isParameterized() {
        return PARAMETERIZED_GATES.contains(this);
    }

    /**
     * Creates a Gate instance for this gate type. For fixed gates, returns a FixedGate instance.
     * For parameterized gates, throws IllegalArgumentException as theta parameter is required.
     *
     * @return Gate instance for fixed gates
     * @throws IllegalArgumentException if this is a parameterized gate type
     */
    public Gate toGate() {
        if (isParameterized()) {
            throw new IllegalArgumentException(
                    "Parameterized gate "
                            + this
                            + " requires theta parameter. Use toGate(double theta) instead.");
        }
        return new FixedGate(this);
    }

    /**
     * Creates a Gate instance for this gate type with the specified parameter. For parameterized
     * gates, returns a ParameterizedGate instance. For fixed gates, ignores the theta parameter and
     * returns a FixedGate instance.
     *
     * @param theta the rotation angle parameter
     * @return Gate instance
     */
    public Gate toGate(double theta) {
        if (isParameterized()) {
            return new ParameterizedGate(this, theta);
        }
        return new FixedGate(this);
    }
}
