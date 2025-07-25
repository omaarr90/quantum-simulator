package com.omaarr90.core.gate;

import com.omaarr90.core.math.Complex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite for gate matrices covering all acceptance criteria.
 */
class GateMatricesTest {
    
    private static final double TOLERANCE = 1e-10;
    
    // Helper method to multiply two matrices
    private Complex[][] multiply(Complex[][] a, Complex[][] b) {
        int rows = a.length;
        int cols = b[0].length;
        int inner = b.length;
        Complex[][] result = new Complex[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = Complex.ZERO;
                for (int k = 0; k < inner; k++) {
                    result[i][j] = result[i][j].add(a[i][k].mul(b[k][j]));
                }
            }
        }
        return result;
    }
    
    // Helper method to compute conjugate transpose (Hermitian conjugate)
    private Complex[][] conjugateTranspose(Complex[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        Complex[][] result = new Complex[cols][rows];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = matrix[i][j].conjugate();
            }
        }
        return result;
    }
    
    // Helper method to check if matrix is approximately identity
    private void assertIsIdentity(Complex[][] matrix, String gateName) {
        int size = matrix.length;
        assertEquals(size, matrix[0].length, gateName + " matrix should be square");
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    // Diagonal elements should be 1
                    assertEquals(1.0, matrix[i][j].real(), TOLERANCE, 
                        gateName + " diagonal element [" + i + "," + j + "] real part");
                    assertEquals(0.0, matrix[i][j].imaginary(), TOLERANCE,
                        gateName + " diagonal element [" + i + "," + j + "] imaginary part");
                } else {
                    // Off-diagonal elements should be 0
                    assertEquals(0.0, matrix[i][j].real(), TOLERANCE,
                        gateName + " off-diagonal element [" + i + "," + j + "] real part");
                    assertEquals(0.0, matrix[i][j].imaginary(), TOLERANCE,
                        gateName + " off-diagonal element [" + i + "," + j + "] imaginary part");
                }
            }
        }
    }
    
    // Helper method to check if two matrices are approximately equal
    private void assertMatricesEqual(Complex[][] expected, Complex[][] actual, String message) {
        assertEquals(expected.length, actual.length, message + " - row count mismatch");
        assertEquals(expected[0].length, actual[0].length, message + " - column count mismatch");
        
        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[0].length; j++) {
                assertEquals(expected[i][j].real(), actual[i][j].real(), TOLERANCE,
                    message + " - element [" + i + "," + j + "] real part");
                assertEquals(expected[i][j].imaginary(), actual[i][j].imaginary(), TOLERANCE,
                    message + " - element [" + i + "," + j + "] imaginary part");
            }
        }
    }
    
    @Test
    @DisplayName("All fixed gate matrices are unitary (U† U = I)")
    void testUnitarity() {
        for (var entry : FixedGate.GATE_MATRICES.entrySet()) {
            GateType gateType = entry.getKey();
            Complex[][] matrix = entry.getValue();
            
            Complex[][] conjugateTranspose = conjugateTranspose(matrix);
            Complex[][] product = multiply(conjugateTranspose, matrix);
            
            assertIsIdentity(product, gateType.name());
        }
    }
    
    @Test
    @DisplayName("Involution checks: H·H = I, X·X = I, Z·Z = I, SWAP·SWAP = I")
    void testInvolutions() {
        // Test H·H = I
        Complex[][] hMatrix = FixedGate.GATE_MATRICES.get(GateType.H);
        Complex[][] hSquared = multiply(hMatrix, hMatrix);
        assertIsIdentity(hSquared, "H·H");
        
        // Test X·X = I
        Complex[][] xMatrix = FixedGate.GATE_MATRICES.get(GateType.X);
        Complex[][] xSquared = multiply(xMatrix, xMatrix);
        assertIsIdentity(xSquared, "X·X");
        
        // Test Z·Z = I
        Complex[][] zMatrix = FixedGate.GATE_MATRICES.get(GateType.Z);
        Complex[][] zSquared = multiply(zMatrix, zMatrix);
        assertIsIdentity(zSquared, "Z·Z");
        
        // Test SWAP·SWAP = I
        Complex[][] swapMatrix = FixedGate.GATE_MATRICES.get(GateType.SWAP);
        Complex[][] swapSquared = multiply(swapMatrix, swapMatrix);
        assertIsIdentity(swapSquared, "SWAP·SWAP");
    }
    
    @Test
    @DisplayName("RX(π) equals X up to global phase")
    void testRxPiEqualsX() {
        ParameterizedGate rxPi = new ParameterizedGate(GateType.RX, Math.PI);
        Complex[][] rxPiMatrix = rxPi.matrix();
        Complex[][] xMatrix = FixedGate.GATE_MATRICES.get(GateType.X);
        
        // RX(π) = -i * X, so we need to check if rxPiMatrix = -i * xMatrix
        Complex minusI = Complex.I.scale(-1.0);
        Complex[][] expectedMatrix = new Complex[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                expectedMatrix[i][j] = xMatrix[i][j].mul(minusI);
            }
        }
        
        assertMatricesEqual(expectedMatrix, rxPiMatrix, "RX(π) should equal -i*X");
    }
    
    @Test
    @DisplayName("RZ(2π) equals identity")
    void testRz2PiEqualsIdentity() {
        ParameterizedGate rz2Pi = new ParameterizedGate(GateType.RZ, 2 * Math.PI);
        Complex[][] rz2PiMatrix = rz2Pi.matrix();
        
        // RZ(2π) should be identity up to global phase
        // Actually RZ(2π) = -I (global phase of -1), so let's check the structure
        Complex[][] expectedMatrix = {
            {Complex.ONE.scale(-1.0), Complex.ZERO},
            {Complex.ZERO, Complex.ONE.scale(-1.0)}
        };
        
        assertMatricesEqual(expectedMatrix, rz2PiMatrix, "RZ(2π) should equal -I");
    }
    
    @Test
    @DisplayName("Parameterized gate validation - non-rotation gates should throw")
    void testParameterizedValidation() {
        // Test that constructing ParameterizedGate with non-rotation gates throws
        assertThrows(IllegalArgumentException.class, 
            () -> new ParameterizedGate(GateType.H, Math.PI),
            "ParameterizedGate with H should throw IllegalArgumentException");
            
        assertThrows(IllegalArgumentException.class,
            () -> new ParameterizedGate(GateType.X, Math.PI),
            "ParameterizedGate with X should throw IllegalArgumentException");
            
        assertThrows(IllegalArgumentException.class,
            () -> new ParameterizedGate(GateType.CX, Math.PI),
            "ParameterizedGate with CX should throw IllegalArgumentException");
    }
    
    @Test
    @DisplayName("Fixed gate validation - rotation gates should throw")
    void testFixedGateValidation() {
        // Test that constructing FixedGate with rotation gates throws
        assertThrows(IllegalArgumentException.class,
            () -> new FixedGate(GateType.RX),
            "FixedGate with RX should throw IllegalArgumentException");
            
        assertThrows(IllegalArgumentException.class,
            () -> new FixedGate(GateType.RY),
            "FixedGate with RY should throw IllegalArgumentException");
            
        assertThrows(IllegalArgumentException.class,
            () -> new FixedGate(GateType.RZ),
            "FixedGate with RZ should throw IllegalArgumentException");
    }
    
    @Test
    @DisplayName("Immutability smoke test - GATE_MATRICES map cannot be modified")
    void testImmutability() {
        // Test that GATE_MATRICES is unmodifiable
        assertThrows(UnsupportedOperationException.class,
            () -> FixedGate.GATE_MATRICES.put(GateType.H, new Complex[2][2]),
            "GATE_MATRICES should be unmodifiable");
            
        assertThrows(UnsupportedOperationException.class,
            () -> FixedGate.GATE_MATRICES.clear(),
            "GATE_MATRICES should be unmodifiable");
    }
    
    @Test
    @DisplayName("Rotation gates are unitary")
    void testRotationGateUnitarity() {
        double[] testAngles = {0.0, Math.PI/4, Math.PI/2, Math.PI, 2*Math.PI};
        GateType[] rotationGates = {GateType.RX, GateType.RY, GateType.RZ};
        
        for (GateType gateType : rotationGates) {
            for (double angle : testAngles) {
                ParameterizedGate gate = new ParameterizedGate(gateType, angle);
                Complex[][] matrix = gate.matrix();
                
                Complex[][] conjugateTranspose = conjugateTranspose(matrix);
                Complex[][] product = multiply(conjugateTranspose, matrix);
                
                assertIsIdentity(product, gateType.name() + "(" + angle + ")");
            }
        }
    }
    
    @Test
    @DisplayName("Matrix caching works correctly")
    void testMatrixCaching() {
        ParameterizedGate gate = new ParameterizedGate(GateType.RX, Math.PI/2);
        
        // Multiple calls to matrix() should return the same object reference
        Complex[][] matrix1 = gate.matrix();
        Complex[][] matrix2 = gate.matrix();
        
        assertSame(matrix1, matrix2, "Matrix should be cached and return same reference");
    }
}