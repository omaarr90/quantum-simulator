package com.omaarr90.core.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComplexTest {

    private static final double EPSILON = 1e-9;

    @Test
    void testAddition() {
        Complex a = new Complex(2.0, 3.0);
        Complex b = new Complex(1.0, -1.0);
        Complex result = a.add(b);
        assertEquals(3.0, result.real());
        assertEquals(2.0, result.imaginary());
    }

    @Test
    void testSubtraction() {
        Complex a = new Complex(5.0, 7.0);
        Complex b = new Complex(2.0, 3.0);
        Complex result = a.sub(b);
        assertEquals(3.0, result.real(), EPSILON);
        assertEquals(4.0, result.imaginary(), EPSILON);
    }

    @Test
    void testSubtractionWithNegativeResult() {
        Complex a = new Complex(1.0, 2.0);
        Complex b = new Complex(3.0, 5.0);
        Complex result = a.sub(b);
        assertEquals(-2.0, result.real(), EPSILON);
        assertEquals(-3.0, result.imaginary(), EPSILON);
    }

    @Test
    void testMultiplication() {
        Complex a = new Complex(1, 2);
        Complex b = new Complex(3, 4);
        Complex result = a.mul(b);
        assertEquals(-5.0, result.real(), EPSILON);
        assertEquals(10.0, result.imaginary(), EPSILON);
    }

    @Test
    void testDivision() {
        Complex a = new Complex(1, 2);
        Complex b = new Complex(3, 4);
        Complex result = a.div(b);
        assertEquals(0.44, result.real(), EPSILON);
        assertEquals(0.08, result.imaginary(), EPSILON);
    }

    @Test
    void testDivisionByZero() {
        Complex a = new Complex(1, 2);
        Complex zero = Complex.ZERO;
        assertThrows(ArithmeticException.class, () -> a.div(zero));
    }

    @Test
    void testDivisionIdentity() {
        Complex z = new Complex(3, 4);
        Complex result = z.div(z);
        assertEquals(1.0, result.real(), EPSILON);
        assertEquals(0.0, result.imaginary(), EPSILON);
    }

    @Test
    void testScale() {
        Complex z = new Complex(2, 3);
        Complex result = z.scale(2.5);
        assertEquals(5.0, result.real(), EPSILON);
        assertEquals(7.5, result.imaginary(), EPSILON);
    }

    @Test
    void testConjugate() {
        Complex z = new Complex(3, 4);
        Complex conj = z.conjugate();
        assertEquals(3.0, conj.real(), EPSILON);
        assertEquals(-4.0, conj.imaginary(), EPSILON);
    }

    @Test
    void testAbs() {
        Complex z = new Complex(3, 4);
        assertEquals(5.0, z.abs(), EPSILON);
    }

    @Test
    void testAbs2() {
        Complex z = new Complex(3, 4);
        assertEquals(25.0, z.abs2(), EPSILON);
    }


    @Test
    void testPolarConversion() {
        Complex z = new Complex(Math.sqrt(3), 1.0);
        Complex.Polar p = z.toPolar();
        assertEquals(2.0, p.r(), EPSILON);
        assertEquals(Math.PI / 6, p.theta(), EPSILON);
    }

    @Test
    void testFromPolar() {
        Complex z = Complex.fromPolar(2.0, Math.PI / 6);
        assertEquals(Math.sqrt(3), z.real(), EPSILON);
        assertEquals(1.0, z.imaginary(), EPSILON);
    }

    @Test
    void testConstants() {
        assertEquals(0.0, Complex.ZERO.real());
        assertEquals(0.0, Complex.ZERO.imaginary());
        
        assertEquals(1.0, Complex.ONE.real());
        assertEquals(0.0, Complex.ONE.imaginary());
        
        assertEquals(0.0, Complex.I.real());
        assertEquals(1.0, Complex.I.imaginary());
    }

    @Test
    void testEdgeCasesWithNaN() {
        Complex z = new Complex(Double.NaN, 1.0);
        assertTrue(Double.isNaN(z.abs()));
        assertTrue(Double.isNaN(z.abs2()));
    }

    @Test
    void testEdgeCasesWithInfinity() {
        Complex z = new Complex(Double.POSITIVE_INFINITY, 1.0);
        assertEquals(Double.POSITIVE_INFINITY, z.abs());
        assertEquals(Double.POSITIVE_INFINITY, z.abs2());
    }

    @Test
    void testAlgebraicIdentities() {
        Complex z1 = new Complex(2, 3);
        Complex z2 = new Complex(1, -2);
        
        // Test (z1 + z2) - z2 = z1
        Complex result1 = z1.add(z2).sub(z2);
        assertEquals(z1.real(), result1.real(), EPSILON);
        assertEquals(z1.imaginary(), result1.imaginary(), EPSILON);
        
        // Test z1 * z2 / z2 = z1 (when z2 != 0)
        Complex result2 = z1.mul(z2).div(z2);
        assertEquals(z1.real(), result2.real(), EPSILON);
        assertEquals(z1.imaginary(), result2.imaginary(), EPSILON);
        
        // Test |z * conj(z)| = |z|²
        Complex product = z1.mul(z1.conjugate());
        assertEquals(z1.abs2(), product.real(), EPSILON);
        assertEquals(0.0, product.imaginary(), EPSILON);
    }

    @Test
    void testMethodChaining() {
        Complex z = new Complex(1, 1);
        // (1+i) + i = (1+2i)
        // (1+2i) * 2 = (2+4i)  
        // (2+4i) - 1 = (1+4i)
        Complex result = z.add(Complex.I).mul(new Complex(2, 0)).sub(Complex.ONE);
        assertEquals(1.0, result.real(), EPSILON);
        assertEquals(4.0, result.imaginary(), EPSILON);
    }

    @Test
    void testToString() {
        Complex z1 = new Complex(3, 4);
        assertEquals("(3.0 + 4.0i)", z1.toString());
        
        Complex z2 = new Complex(3, -4);
        assertEquals("(3.0 - 4.0i)", z2.toString());
        
        Complex z3 = new Complex(-3, 4);
        assertEquals("(-3.0 + 4.0i)", z3.toString());
    }
}
