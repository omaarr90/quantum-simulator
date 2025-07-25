package com.omaarr90.core.gate;

import com.omaarr90.core.math.Complex;
import java.util.Collections;
import java.util.Map;

/**
 * Record for immutable quantum gates with fixed matrices.
 */
public record FixedGate(GateType type) implements Gate {
    
    // Gate matrices as immutable constants
    private static final Complex ZERO = Complex.ZERO;
    private static final Complex ONE = Complex.ONE;
    private static final Complex I = Complex.I;
    private static final Complex MINUS_I = I.scale(-1.0);
    private static final Complex INV_SQRT2 = ONE.scale(1.0 / Math.sqrt(2.0));
    
    // Pauli matrices and other fixed gates
    private static final Complex[][] H_MATRIX = {
        {INV_SQRT2, INV_SQRT2},
        {INV_SQRT2, INV_SQRT2.scale(-1.0)}
    };
    
    private static final Complex[][] X_MATRIX = {
        {ZERO, ONE},
        {ONE, ZERO}
    };
    
    private static final Complex[][] Y_MATRIX = {
        {ZERO, MINUS_I},
        {I, ZERO}
    };
    
    private static final Complex[][] Z_MATRIX = {
        {ONE, ZERO},
        {ZERO, ONE.scale(-1.0)}
    };
    
    // Two-qubit gates
    private static final Complex[][] CX_MATRIX = {
        {ONE, ZERO, ZERO, ZERO},
        {ZERO, ONE, ZERO, ZERO},
        {ZERO, ZERO, ZERO, ONE},
        {ZERO, ZERO, ONE, ZERO}
    };
    
    private static final Complex[][] CZ_MATRIX = {
        {ONE, ZERO, ZERO, ZERO},
        {ZERO, ONE, ZERO, ZERO},
        {ZERO, ZERO, ONE, ZERO},
        {ZERO, ZERO, ZERO, ONE.scale(-1.0)}
    };
    
    private static final Complex[][] SWAP_MATRIX = {
        {ONE, ZERO, ZERO, ZERO},
        {ZERO, ZERO, ONE, ZERO},
        {ZERO, ONE, ZERO, ZERO},
        {ZERO, ZERO, ZERO, ONE}
    };
    
    // Immutable map of gate matrices
    public static final Map<GateType, Complex[][]> GATE_MATRICES = Map.of(
            GateType.H, H_MATRIX,
            GateType.X, X_MATRIX,
            GateType.Y, Y_MATRIX,
            GateType.Z, Z_MATRIX,
            GateType.CX, CX_MATRIX,
            GateType.CZ, CZ_MATRIX,
            GateType.SWAP, SWAP_MATRIX
    );
    
    public FixedGate {
        if (!GATE_MATRICES.containsKey(type)) {
            throw new IllegalArgumentException("Gate type " + type + " is not a fixed gate");
        }
    }
    
    @Override
    public Complex[][] matrix() {
        return GATE_MATRICES.get(type);
    }
}