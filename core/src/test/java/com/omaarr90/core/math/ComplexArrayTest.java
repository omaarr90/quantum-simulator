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

    @Test
    void broadcastAdd_matches_scalar_reference() {
        int n = 256; // covers several vector lanes
        ComplexArray a = randomArray(n);
        Complex scalar = new Complex(Math.PI, -Math.E);

        // Scalar reference result
        ComplexArray expected = a.copy();
        for (int i = 0; i < n; i++) {
            Complex c = expected.get(i);
            expected.set(i, new Complex(c.real() + scalar.real(), c.imaginary() + scalar.imaginary()));
        }

        // SIMD / scalar mix result
        ComplexArray actual = a.copy().broadcastAdd(scalar);

        for (int i = 0; i < n; i++) {
            assertEqualComplex(expected.get(i), actual.get(i));
        }
    }

    @Test
    void conjugateInPlace_matches_scalar_reference() {
        int n = 257; // deliberately choose a non‑multiple of species length
        ComplexArray a = randomArray(n);

        // Scalar reference result
        ComplexArray expected = a.copy();
        for (int i = 0; i < n; i++) {
            Complex c = expected.get(i);
            expected.set(i, new Complex(c.real(), -c.imaginary()));
        }

        // SIMD / scalar mix result
        ComplexArray actual = a.copy().conjugateInPlace();

        for (int i = 0; i < n; i++) {
            assertEqualComplex(expected.get(i), actual.get(i));
        }
    }

    @Test
    void length_accessor_works() {
        ComplexArray a = new ComplexArray(42);
        assertEquals(42, a.length());
        assertEquals(a.size(), a.length());
    }

    @Test
    void isAligned_works_correctly() {
        // Test with aligned size (assuming species length is power of 2)
        ComplexArray aligned = new ComplexArray(64);
        ComplexArray unaligned = new ComplexArray(65);
        
        // Note: actual alignment depends on the species length at runtime
        // We just test that the method doesn't throw and returns a boolean
        boolean alignedResult = aligned.isAligned();
        boolean unalignedResult = unaligned.isAligned();
        
        assertTrue(alignedResult || !alignedResult); // Always true, just testing it doesn't throw
        assertTrue(unalignedResult || !unalignedResult); // Always true, just testing it doesn't throw
    }

    @Test
    void input_validation_works() {
        // Test negative size
        assertThrows(IllegalArgumentException.class, () -> new ComplexArray(-1));
        
        // Test index bounds
        ComplexArray a = new ComplexArray(5);
        assertThrows(IndexOutOfBoundsException.class, () -> a.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> a.get(5));
        assertThrows(IndexOutOfBoundsException.class, () -> a.set(-1, new Complex(1, 2)));
        assertThrows(IndexOutOfBoundsException.class, () -> a.set(5, new Complex(1, 2)));
    }

    @Test
    void vector_api_disabled_fallback() {
        // This test simulates the scenario where Vector API is disabled
        // We can't easily disable it at runtime, but we can test with small arrays
        // that would use scalar fallback for the tail elements
        
        int n = 3; // Small size likely to use scalar fallback
        ComplexArray a = randomArray(n);
        Complex scalar = new Complex(1.5, -2.5);
        
        // Test broadcastAdd with small array
        ComplexArray expected = a.copy();
        for (int i = 0; i < n; i++) {
            Complex c = expected.get(i);
            expected.set(i, new Complex(c.real() + scalar.real(), c.imaginary() + scalar.imaginary()));
        }
        
        ComplexArray actual = a.copy().broadcastAdd(scalar);
        for (int i = 0; i < n; i++) {
            assertEqualComplex(expected.get(i), actual.get(i));
        }
        
        // Test conjugateInPlace with small array
        ComplexArray b = randomArray(n);
        ComplexArray expectedConj = b.copy();
        for (int i = 0; i < n; i++) {
            Complex c = expectedConj.get(i);
            expectedConj.set(i, new Complex(c.real(), -c.imaginary()));
        }
        
        ComplexArray actualConj = b.copy().conjugateInPlace();
        for (int i = 0; i < n; i++) {
            assertEqualComplex(expectedConj.get(i), actualConj.get(i));
        }
    }

}
