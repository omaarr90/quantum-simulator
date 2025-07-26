package com.omaarr90.core.gate;

import com.omaarr90.core.math.Complex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite for gate matrices covering all acceptance criteria.
 */
@SuppressWarnings("deprecation")
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
        GateType[] fixedGates = {GateType.H, GateType.X, GateType.Y, GateType.Z, 
                                GateType.S, GateType.T, GateType.CX, GateType.CZ, GateType.SWAP};
        
        for (GateType gateType : fixedGates) {
            FixedGate gate = new FixedGate(gateType);
            Complex[][] matrix = gate.matrix();
            
            Complex[][] conjugateTranspose = conjugateTranspose(matrix);
            Complex[][] product = multiply(conjugateTranspose, matrix);
            
            assertIsIdentity(product, gateType.name());
        }
    }
    
    @Test
    @DisplayName("Involution checks: H·H = I, X·X = I, Z·Z = I, SWAP·SWAP = I")
    void testInvolutions() {
        // Test H·H = I
        Complex[][] hMatrix = new FixedGate(GateType.H).matrix();
        Complex[][] hSquared = multiply(hMatrix, hMatrix);
        assertIsIdentity(hSquared, "H·H");
        
        // Test X·X = I
        Complex[][] xMatrix = new FixedGate(GateType.X).matrix();
        Complex[][] xSquared = multiply(xMatrix, xMatrix);
        assertIsIdentity(xSquared, "X·X");
        
        // Test Z·Z = I
        Complex[][] zMatrix = new FixedGate(GateType.Z).matrix();
        Complex[][] zSquared = multiply(zMatrix, zMatrix);
        assertIsIdentity(zSquared, "Z·Z");
        
        // Test SWAP·SWAP = I
        Complex[][] swapMatrix = new FixedGate(GateType.SWAP).matrix();
        Complex[][] swapSquared = multiply(swapMatrix, swapMatrix);
        assertIsIdentity(swapSquared, "SWAP·SWAP");
    }
    
    @Test
    @DisplayName("RX(π) equals X up to global phase")
    void testRxPiEqualsX() {
        ParameterizedGate rxPi = new ParameterizedGate(GateType.RX, Math.PI);
        Complex[][] rxPiMatrix = rxPi.matrix();
        Complex[][] xMatrix = new FixedGate(GateType.X).matrix();
        
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
    
    @Test
    @DisplayName("GateType.isParameterized() correctly identifies parameterized gates")
    void testIsParameterized() {
        // Test parameterized gates
        assertTrue(GateType.RX.isParameterized(), "RX should be parameterized");
        assertTrue(GateType.RY.isParameterized(), "RY should be parameterized");
        assertTrue(GateType.RZ.isParameterized(), "RZ should be parameterized");
        
        // Test fixed gates
        assertFalse(GateType.H.isParameterized(), "H should not be parameterized");
        assertFalse(GateType.X.isParameterized(), "X should not be parameterized");
        assertFalse(GateType.Y.isParameterized(), "Y should not be parameterized");
        assertFalse(GateType.Z.isParameterized(), "Z should not be parameterized");
        assertFalse(GateType.S.isParameterized(), "S should not be parameterized");
        assertFalse(GateType.T.isParameterized(), "T should not be parameterized");
        assertFalse(GateType.CX.isParameterized(), "CX should not be parameterized");
        assertFalse(GateType.CZ.isParameterized(), "CZ should not be parameterized");
        assertFalse(GateType.SWAP.isParameterized(), "SWAP should not be parameterized");
    }
    
    @Test
    @DisplayName("All gate matrices have determinant ≈ 1 (unitary property)")
    void testDeterminantIsOne() {
        // Test fixed gates
        GateType[] fixedGates = {GateType.H, GateType.X, GateType.Y, GateType.Z, 
                                GateType.S, GateType.T, GateType.CX, GateType.CZ, GateType.SWAP};
        
        for (GateType gateType : fixedGates) {
            FixedGate gate = new FixedGate(gateType);
            Complex[][] matrix = gate.matrix();
            
            Complex determinant = calculateDeterminant(matrix);
            double detMagnitude = determinant.abs();
            
            assertEquals(1.0, detMagnitude, TOLERANCE, 
                gateType.name() + " matrix determinant magnitude should be 1");
        }
        
        // Test parameterized gates at various angles
        double[] testAngles = {0.0, Math.PI/4, Math.PI/2, Math.PI, 2*Math.PI};
        GateType[] rotationGates = {GateType.RX, GateType.RY, GateType.RZ};
        
        for (GateType gateType : rotationGates) {
            for (double angle : testAngles) {
                ParameterizedGate gate = new ParameterizedGate(gateType, angle);
                Complex[][] matrix = gate.matrix();
                
                Complex determinant = calculateDeterminant(matrix);
                double detMagnitude = determinant.abs();
                
                assertEquals(1.0, detMagnitude, TOLERANCE,
                    gateType.name() + "(" + angle + ") matrix determinant magnitude should be 1");
            }
        }
    }
    
    @Test
    @DisplayName("Pauli relations: X·Y = iZ, Y·Z = iX, Z·X = iY")
    void testPauliRelations() {
        Complex[][] xMatrix = new FixedGate(GateType.X).matrix();
        Complex[][] yMatrix = new FixedGate(GateType.Y).matrix();
        Complex[][] zMatrix = new FixedGate(GateType.Z).matrix();
        
        // Test X·Y = iZ
        Complex[][] xyProduct = multiply(xMatrix, yMatrix);
        Complex[][] expectedIZ = new Complex[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                expectedIZ[i][j] = zMatrix[i][j].mul(Complex.I);
            }
        }
        assertMatricesEqual(expectedIZ, xyProduct, "X·Y should equal i·Z");
        
        // Test Y·Z = iX
        Complex[][] yzProduct = multiply(yMatrix, zMatrix);
        Complex[][] expectedIX = new Complex[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                expectedIX[i][j] = xMatrix[i][j].mul(Complex.I);
            }
        }
        assertMatricesEqual(expectedIX, yzProduct, "Y·Z should equal i·X");
        
        // Test Z·X = iY
        Complex[][] zxProduct = multiply(zMatrix, xMatrix);
        Complex[][] expectedIY = new Complex[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                expectedIY[i][j] = yMatrix[i][j].mul(Complex.I);
            }
        }
        assertMatricesEqual(expectedIY, zxProduct, "Z·X should equal i·Y");
    }
    
    @Test
    @DisplayName("S and T gate properties")
    void testSAndTGateProperties() {
        Complex[][] sMatrix = new FixedGate(GateType.S).matrix();
        Complex[][] tMatrix = new FixedGate(GateType.T).matrix();
        Complex[][] zMatrix = new FixedGate(GateType.Z).matrix();
        
        // Test S² = Z
        Complex[][] sSquared = multiply(sMatrix, sMatrix);
        assertMatricesEqual(zMatrix, sSquared, "S² should equal Z");
        
        // Test T⁴ = Z
        Complex[][] tSquared = multiply(tMatrix, tMatrix);
        Complex[][] tFourth = multiply(tSquared, tSquared);
        assertMatricesEqual(zMatrix, tFourth, "T⁴ should equal Z");
        
        // Test T² = S
        assertMatricesEqual(sMatrix, tSquared, "T² should equal S");
        
        // Test S and T are unitary
        Complex[][] sConjugateTranspose = conjugateTranspose(sMatrix);
        Complex[][] sProduct = multiply(sConjugateTranspose, sMatrix);
        assertIsIdentity(sProduct, "S");
        
        Complex[][] tConjugateTranspose = conjugateTranspose(tMatrix);
        Complex[][] tProduct = multiply(tConjugateTranspose, tMatrix);
        assertIsIdentity(tProduct, "T");
    }
    
    
    // Helper method to calculate determinant for 2x2 and 4x4 matrices
    private Complex calculateDeterminant(Complex[][] matrix) {
        int size = matrix.length;
        
        if (size == 2) {
            // det(2x2) = ad - bc
            return matrix[0][0].mul(matrix[1][1]).sub(matrix[0][1].mul(matrix[1][0]));
        } else if (size == 4) {
            // For 4x4 matrix, use cofactor expansion along first row
            Complex det = Complex.ZERO;
            for (int j = 0; j < 4; j++) {
                Complex cofactor = matrix[0][j].mul(calculateCofactor(matrix, 0, j));
                if (j % 2 == 0) {
                    det = det.add(cofactor);
                } else {
                    det = det.sub(cofactor);
                }
            }
            return det;
        } else {
            throw new IllegalArgumentException("Determinant calculation only supports 2x2 and 4x4 matrices");
        }
    }
    
    // Helper method to calculate cofactor for 4x4 matrix
    private Complex calculateCofactor(Complex[][] matrix, int row, int col) {
        Complex[][] minor = new Complex[3][3];
        int minorRow = 0;
        
        for (int i = 0; i < 4; i++) {
            if (i == row) continue;
            int minorCol = 0;
            for (int j = 0; j < 4; j++) {
                if (j == col) continue;
                minor[minorRow][minorCol] = matrix[i][j];
                minorCol++;
            }
            minorRow++;
        }
        
        // Calculate 3x3 determinant
        return minor[0][0].mul(minor[1][1].mul(minor[2][2]).sub(minor[1][2].mul(minor[2][1])))
            .sub(minor[0][1].mul(minor[1][0].mul(minor[2][2]).sub(minor[1][2].mul(minor[2][0]))))
            .add(minor[0][2].mul(minor[1][0].mul(minor[2][1]).sub(minor[1][1].mul(minor[2][0]))));
    }
}