package com.omaarr90.core.gate;

import com.omaarr90.core.math.Complex;
import java.util.Set;

/**
 * Record for parameterized quantum gates (rotation gates).
 */
public record ParameterizedGate(GateType type, double theta, Complex[][] matrix) implements Gate {
    
    private static final Set<GateType> ROTATION_GATES = Set.of(GateType.RX, GateType.RY, GateType.RZ);
    
    // Constants for matrix computation
    private static final Complex ZERO = Complex.ZERO;
    private static final Complex ONE = Complex.ONE;
    private static final Complex I = Complex.I;
    private static final Complex MINUS_I = I.scale(-1.0);
    
    // Constructor that takes only type and theta, computes matrix
    public ParameterizedGate(GateType type, double theta) {
        this(type, theta, computeMatrix(type, theta));
    }
    
    // Compact constructor for validation
    public ParameterizedGate {
        if (!ROTATION_GATES.contains(type)) {
            throw new IllegalArgumentException("Gate type " + type + " is not a parameterized gate");
        }
    }
    
    private static Complex[][] computeMatrix(GateType type, double theta) {
        double halfTheta = theta / 2.0;
        Complex cosHalf = ONE.scale(Math.cos(halfTheta));
        Complex sinHalf = ONE.scale(Math.sin(halfTheta));
        Complex iSinHalf = I.scale(Math.sin(halfTheta));
        Complex minusISinHalf = MINUS_I.scale(Math.sin(halfTheta));
        
        return switch (type) {
            case RX -> {
                // RX(θ) = cos(θ/2) I - i sin(θ/2) X
                // = [[cos(θ/2), -i sin(θ/2)], [-i sin(θ/2), cos(θ/2)]]
                yield new Complex[][] {
                    {cosHalf, minusISinHalf},
                    {minusISinHalf, cosHalf}
                };
            }
            case RY -> {
                // RY(θ) = cos(θ/2) I - i sin(θ/2) Y
                // = [[cos(θ/2), -sin(θ/2)], [sin(θ/2), cos(θ/2)]]
                yield new Complex[][] {
                    {cosHalf, sinHalf.scale(-1.0)},
                    {sinHalf, cosHalf}
                };
            }
            case RZ -> {
                // RZ(θ) = cos(θ/2) I - i sin(θ/2) Z
                // = [[cos(θ/2) - i sin(θ/2), 0], [0, cos(θ/2) + i sin(θ/2)]]
                Complex expMinusIHalf = cosHalf.add(minusISinHalf);
                Complex expPlusIHalf = cosHalf.add(iSinHalf);
                yield new Complex[][] {
                    {expMinusIHalf, ZERO},
                    {ZERO, expPlusIHalf}
                };
            }
            default -> throw new IllegalArgumentException("Unsupported rotation gate: " + type);
        };
    }
}