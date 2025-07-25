package com.omaarr90.core.gate;

import com.omaarr90.core.math.Complex;

/**
 * Sealed interface for quantum gates providing matrix representation.
 */
public sealed interface Gate permits FixedGate, ParameterizedGate {
    
    /**
     * Returns the matrix representation of this gate.
     * 
     * @return the gate matrix as Complex[][]
     */
    Complex[][] matrix();
}