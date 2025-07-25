package com.omaarr90.core.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ComplexArray} covering the key arithmetic operations
 * and edge‑cases.  All assertions are performed with a 1e‑12 tolerance.
 */
public class ComplexArrayTest {

    private static final double EPS = 1e-12;

    /* --------------------------------------------------------------------- */
    /* Helpers                                                               */
    /* --------------------------------------------------------------------- */

    private static ComplexArray randomArray(int n) {
        ComplexArray a = new ComplexArray(n);
        for (int i = 0; i < n; i++) {
            a.set(i, new Complex(Math.random(), Math.random()));
        }
        return a;
    }

    private static void assertEqualComplex(Complex expected, Complex actual) {
        assertEquals(expected.real(), actual.real(), EPS);
        assertEquals(expected.imaginary(), actual.imaginary(), EPS);
    }

    /* --------------------------------------------------------------------- */
    /* Tests                                                                  */
    /* --------------------------------------------------------------------- */

    @Test
    void addition_matches_scalar_reference() {
        int n = 256; // covers several vector lanes
        ComplexArray a = randomArray(n);
        ComplexArray b = randomArray(n);

        // Scalar reference result
        ComplexArray expected = a.copy();
        for (int i = 0; i < n; i++) {
            Complex c1 = expected.get(i);
            Complex c2 = b.get(i);
            expected.set(i, new Complex(c1.real() + c2.real(), c1.imaginary() + c2.imaginary()));
        }

        // SIMD / scalar mix result
        ComplexArray actual = a.copy().addInPlace(b);

        for (int i = 0; i < n; i++) {
            assertEqualComplex(expected.get(i), actual.get(i));
        }
    }

    @Test
    void multiplication_matches_scalar_reference() {
        int n = 257; // deliberately choose a non‑multiple of species length
        ComplexArray a = randomArray(n);
        ComplexArray b = randomArray(n);

        ComplexArray expected = a.copy();
        for (int i = 0; i < n; i++) {
            Complex c1 = expected.get(i);
            Complex c2 = b.get(i);
            double re = c1.real() * c2.real() - c1.imaginary() * c2.imaginary();
            double im = c1.real() * c2.imaginary() + c1.imaginary() * c2.real();
            expected.set(i, new Complex(re, im));
        }

        ComplexArray actual = a.copy().multiplyInPlace(b);

        for (int i = 0; i < n; i++) {
            assertEqualComplex(expected.get(i), actual.get(i));
        }
    }

    @Test
    void scale_by_real_scalar() {
        int n = 17;
        double k = Math.PI;
        ComplexArray a = randomArray(n);
        ComplexArray expected = a.copy();
        for (int i = 0; i < n; i++) {
            Complex c = expected.get(i);
            expected.set(i, new Complex(c.real() * k, c.imaginary() * k));
        }
        ComplexArray actual = a.copy().scaleInPlace(k);
        for (int i = 0; i < n; i++) {
            assertEqualComplex(expected.get(i), actual.get(i));
        }
    }

    @Test
    void multiply_by_complex_scalar() {
        int n = 64;
        Complex s = new Complex(Math.E, -Math.sqrt(2));
        ComplexArray a = randomArray(n);
        ComplexArray expected = a.copy();
        for (int i = 0; i < n; i++) {
            Complex c = expected.get(i);
            double re = c.real() * s.real() - c.imaginary() * s.imaginary();
            double im = c.real() * s.imaginary() + c.imaginary() * s.real();
            expected.set(i, new Complex(re, im));
        }
        ComplexArray actual = a.copy().multiplyInPlace(s);
        for (int i = 0; i < n; i++) {
            assertEqualComplex(expected.get(i), actual.get(i));
        }
    }

    @Test
    void size_mismatch_throws() {
        ComplexArray a = new ComplexArray(4);
        ComplexArray b = new ComplexArray(5);
        assertThrows(IllegalArgumentException.class, () -> a.addInPlace(b));
        assertThrows(IllegalArgumentException.class, () -> a.multiplyInPlace(b));
    }

}
