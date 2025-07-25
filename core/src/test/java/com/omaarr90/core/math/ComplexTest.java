package com.omaarr90.core.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComplexTest {

    @Test
    void testAddition() {
        Complex a = new Complex(2.0, 3.0);
        Complex b = new Complex(1.0, -1.0);
        Complex result = a.add(b);
        assertEquals(3.0, result.real());
        assertEquals(2.0, result.imaginary());
    }

    @Test
    void testMultiplication() {
        Complex a = new Complex(1, 2);
        Complex b = new Complex(3, 4);
        Complex result = a.multiply(b);
        assertEquals(-5.0, result.real(), 1e-9);
        assertEquals(10.0, result.imaginary(), 1e-9);
    }

    @Test
    void testNorm() {
        Complex z = new Complex(3, 4);
        assertEquals(25.0, z.norm());
        assertEquals(5.0, z.abs(), 1e-9);
    }

    @Test
    void testPolarConversion() {
        Complex z = new Complex(Math.sqrt(3), 1.0);
        Complex.Polar p = z.toPolar();
        assertEquals(2.0, p.r(), 1e-9);
        assertEquals(Math.PI / 6, p.theta(), 1e-9);
    }
}
