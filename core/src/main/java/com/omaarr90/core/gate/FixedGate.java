package com.omaarr90.core.gate;

import com.omaarr90.core.math.Complex;

/** Record for immutable quantum gates with fixed matrices. */
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

    // Phase gates
    private static final Complex[][] S_MATRIX = {
        {ONE, ZERO},
        {ZERO, I}
    };

    private static final Complex[][] SDG_MATRIX = {
        {ONE, ZERO},
        {ZERO, MINUS_I}
    };

    private static final Complex[][] T_MATRIX = {
        {ONE, ZERO},
        {ZERO, ONE.scale(Math.cos(Math.PI / 4.0)).add(I.scale(Math.sin(Math.PI / 4.0)))}
    };

    private static final Complex[][] TDG_MATRIX = {
        {ONE, ZERO},
        {ZERO, ONE.scale(Math.cos(Math.PI / 4.0)).add(I.scale(-Math.sin(Math.PI / 4.0)))}
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

    public FixedGate {
        // Validate that the gate type is a fixed gate (not parameterized)
        if (type.isParameterized()) {
            throw new IllegalArgumentException("Gate type " + type + " is not a fixed gate");
        }
    }

    @Override
    public Complex[][] matrix() {
        return switch (type) {
            case H -> H_MATRIX;
            case X -> X_MATRIX;
            case Y -> Y_MATRIX;
            case Z -> Z_MATRIX;
            case S -> S_MATRIX;
            case SDG -> SDG_MATRIX;
            case T -> T_MATRIX;
            case TDG -> TDG_MATRIX;
            case CX -> CX_MATRIX;
            case CZ -> CZ_MATRIX;
            case SWAP -> SWAP_MATRIX;
            default -> throw new IllegalArgumentException("Unsupported fixed gate type: " + type);
        };
    }
}
